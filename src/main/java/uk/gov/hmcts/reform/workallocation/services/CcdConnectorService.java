package uk.gov.hmcts.reform.workallocation.services;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.reform.workallocation.ccd.CcdClient;
import uk.gov.hmcts.reform.workallocation.exception.CcdConnectionException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
public class CcdConnectorService {

    public static final String FROM_PLACE_HOLDER = "[FROM]";
    public static final String TO_PLACE_HOLDER = "[TO]";

    private final CcdClient ccdClient;

    @Value("${ccd.dry_run}")
    private boolean dryRun;

    @Value("${ccd.ctids}")
    private String ctids;

    private final String queryTemplate = "{\"query\":{\"bool\":{\"must\":[{\"range\":{\"last_modified\":{\"gte\":\""
        + FROM_PLACE_HOLDER + "\", \"lte\":\"" + TO_PLACE_HOLDER + "\"}}}"
        + ",{\"match\":{\"state\":{\"query\": \"Submitted AwaitingHWFDecision DARequested\","
        + "\"operator\": \"or\"}}}]}},"
        + "\"_source\": [\"reference\", \"jurisdiction\", \"state\", \"last_modified\"],"
        + "\"size\": 1000}";

    @Autowired
    public CcdConnectorService(CcdClient ccdClient) {
        this.ccdClient = ccdClient;
    }

    public Map<String, Object> searchCases(String userAuthToken, String serviceToken,
                                           String queryFromDateTime, String queryToDateTime)
            throws CcdConnectionException {
        Map<String, Object> response;
        if (dryRun) {
            log.info("Running dry and not connecting to CCD");
            response = prepareDryResponse();
            return response;
        }
        try {
            response = ccdClient.searchCases(userAuthToken, serviceToken, ctids,
                queryTemplate.replace(FROM_PLACE_HOLDER, queryFromDateTime)
                    .replace(TO_PLACE_HOLDER, queryToDateTime));
            response.put("case_type_id", ctids);
        } catch (Exception e) {
            throw new CcdConnectionException("Failed to connect ccd.", e);
        }
        return response;
    }

    private Map<String, Object> prepareDryResponse() {
        Map<String, Object> response = new HashMap<>();
        response.put("total", 0);
        response.put("cases", new ArrayList<>());
        return response;
    }
}