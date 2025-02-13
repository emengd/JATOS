package services.gui;

import com.google.common.collect.ImmutableMap;
import daos.common.ComponentResultDao;
import daos.common.StudyResultDao;
import daos.common.UserDao;
import models.common.Study;
import models.common.StudyResultStatus;
import models.common.User;
import utils.common.Helpers;
import utils.common.IOUtils;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Service class mostly for Admin controller.
 *
 * @author Kristian Lange
 */
@Singleton
public class AdminService {

    private final UserDao userDao;
    private final StudyResultDao studyResultDao;
    private final ComponentResultDao componentResultDao;
    private final AuthenticationService authenticationService;
    private final IOUtils ioUtils;
    private final UserSessionCacheAccessor userSessionCacheAccessor;

    @Inject
    AdminService(UserDao userDao, StudyResultDao studyResultDao, ComponentResultDao componentResultDao,
            AuthenticationService authenticationService, IOUtils ioUtils,
            UserSessionCacheAccessor userSessionCacheAccessor) {
        this.userDao = userDao;
        this.studyResultDao = studyResultDao;
        this.componentResultDao = componentResultDao;
        this.authenticationService = authenticationService;
        this.ioUtils = ioUtils;
        this.userSessionCacheAccessor = userSessionCacheAccessor;
    }

    public List<Map<String, Object>> getStudiesData(Collection<Study> studyList,
            boolean studyAssetsSizeFlag, boolean resultDataSizeFlag, boolean resultFileSizeFlag) {
        List<Map<String, Object>> studies = new ArrayList<>();
        for (Study study : studyList) {
            int studyResultCount = studyResultDao.countByStudy(study);
            Map<String, Object> studyInfo = new HashMap<>();
            studyInfo.put("id", study.getId());
            studyInfo.put("uuid", study.getUuid());
            studyInfo.put("title", study.getTitle());
            studyInfo.put("active", study.isActive());
            studyInfo.put("studyResultCount", studyResultCount);
            studyInfo.put("members", study.getUserList().stream().map(User::toString).collect(Collectors.toList()));
            if (studyAssetsSizeFlag) {
                studyInfo.put("studyAssetsSize", getStudyAssetDirSize(study));
            } else {
                studyInfo.put("studyAssetsSize", ImmutableMap.of("display", "disabled", "bytes", 0));
            }
            if (resultDataSizeFlag) {
                studyInfo.put("resultDataSize", getResultDataSize(study, studyResultCount));
            } else {
                studyInfo.put("resultDataSize", ImmutableMap.of("display", "disabled", "bytes", 0));
            }
            if (resultFileSizeFlag) {
                studyInfo.put("resultFileSize", getResultFileSize(study, studyResultCount));
            } else {
                studyInfo.put("resultFileSize", ImmutableMap.of("display", "disabled", "bytes", 0));
            }
            Optional<StudyResultStatus> srsOpt = studyResultDao.findLastStarted(study);
            if (srsOpt.isPresent()) {
                studyInfo.put("lastStarted", Helpers.formatDate(srsOpt.get().getStartDate()));
            } else {
                studyInfo.put("lastStarted", "never");
            }
            studies.add(studyInfo);
        }
        return studies;
    }

    public Map<String, Object> getStudyAssetDirSize(Study study) {
        long studyAssetsDirSize = ioUtils.getStudyAssetsDirSize(study.getDirName());
        return ImmutableMap.of(
                "display", Helpers.humanReadableByteCount(studyAssetsDirSize),
                "bytes", studyAssetsDirSize);
    }

    public ImmutableMap<String, Object> getResultDataSize(Study study, int studyResultCount) {
        long resultDataSize = componentResultDao.sizeByStudy(study);
        String resultDataSizePerStudyResultCount = (studyResultCount != 0 ?
                Helpers.humanReadableByteCount(resultDataSize / studyResultCount) : "0 B");
        String resultDataSizeDisplay = Helpers.humanReadableByteCount(resultDataSize)
                + " (" + resultDataSizePerStudyResultCount + ")";
        return ImmutableMap.of(
                "display", resultDataSizeDisplay,
                "bytes", resultDataSize);
    }

    public ImmutableMap<String, Object> getResultFileSize(Study study, int studyResultCount) {
        long resultFileSize = studyResultDao.findIdsByStudyId(study.getId()).stream()
                .mapToLong(ioUtils::getResultUploadDirSize).sum();
        String resultFileSizePerStudyResultCount = studyResultCount != 0 ?
                Helpers.humanReadableByteCount(resultFileSize / studyResultCount) : "0 B";
        String resultFileSizeDisplay = Helpers.humanReadableByteCount(resultFileSize)
                + " (" + resultFileSizePerStudyResultCount + ")";
        return ImmutableMap.of(
                "display", resultFileSizeDisplay,
                "bytes", resultFileSize);
    }

    /**
     * Gets the last seen time of users that were active latest, except the logged in one. It is limited to 'limit'
     * latest users.
     */
    public List<Map<String, String>> getLatestUsers(int limit) {
        List<String> normalizedUsernameList = userDao.findAll().stream()
                .map(User::getUsername)
                .filter(u -> !u.equals(authenticationService.getLoggedInUser().getUsername()))
                .collect(Collectors.toList());

        Map<String, Instant> lastSeenMap = new HashMap<>();
        for (String normalizedUsername : normalizedUsernameList) {
            Optional<Instant> lastSeenOptional = userSessionCacheAccessor.getLastSeen(normalizedUsername);
            lastSeenOptional.ifPresent(lastSeen -> lastSeenMap.put(normalizedUsername, lastSeen));
        }

        List<Map<String, String>> lastSeenMapOrdered = lastSeenMap.entrySet().stream()
                .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                .limit(limit)
                .map(e -> ImmutableMap.of(
                        "username", e.getKey(),
                        "time", Helpers.formatDate(Date.from(e.getValue()))))
                .collect(Collectors.toList());
        return lastSeenMapOrdered;
    }

    public List<Map<String, Object>> getLatestStudyRuns(int limit) {
        return studyResultDao.findLastSeen(limit).stream()
                .map(srs -> ImmutableMap.of(
                        "studyTitle", srs.getStudy().getTitle(),
                        "time", Helpers.formatDate(srs.getLastSeenDate()),
                        "members",
                        srs.getStudy().getUserList().stream().map(User::toString).collect(Collectors.toList())))
                .collect(Collectors.toList());

    }

}
