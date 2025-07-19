package org.cataract.web.application.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.cataract.web.application.service.FindReportCandidateService;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class FindReportCandidateServiceImpl implements FindReportCandidateService {

    public int calculateDistance(String s1, String s2) {
        if (s1 == null || s2 == null) {
            throw new IllegalArgumentException("Inputs must not be null");
        }

        // Always make s1 the shorter
        if (s1.length() > s2.length()) {
            String tmp = s1;
            s1 = s2;
            s2 = tmp;
        }

        int m = s1.length();
        int n = s2.length();

        int[] previous = new int[m + 1];
        int[] current = new int[m + 1];

        // initialize
        for (int i = 0; i <= m; i++) {
            previous[i] = i;
        }

        for (int j = 1; j <= n; j++) {
            current[0] = j;

            for (int i = 1; i <= m; i++) {
                int cost = s1.charAt(i - 1) == s2.charAt(j - 1) ? 0 : 1;

                current[i] = Math.min(
                        Math.min(current[i - 1] + 1,         // insertion
                                previous[i] + 1),           // deletion
                        previous[i - 1] + cost               // substitution
                );
            }

            // swap rows
            int[] temp = previous;
            previous = current;
            current = temp;
        }

        return previous[m];
    }

}
