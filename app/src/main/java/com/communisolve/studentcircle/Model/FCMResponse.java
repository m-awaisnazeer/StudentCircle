package com.communisolve.studentcircle.Model;

import java.util.List;

public class FCMResponse {
    long multicast_id = 0;
    int success = 0;
    int failure = 0;
    int canonical_ids = 0;
    List<FCMResult> results = null;
    long message_id = 0;
}
