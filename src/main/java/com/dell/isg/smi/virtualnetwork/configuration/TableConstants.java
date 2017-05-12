/**
 * Copyright © 2017 DELL Inc. or its subsidiaries.  All Rights Reserved.
 */
package com.dell.isg.smi.virtualnetwork.configuration;

/**
 * The Interface TableConstants.
 */
public interface TableConstants {

    String C_ID = "id";

    // LocalizedMessages table
    String T_LOCALIZED_MESSAGES = "localized_msg_codes";
    String SEQ_LOCALIZED_MESSAGES = "localized_msg_codes_id_seq";
    String LOCALIZED_MESSAGES_ID = "id";
    String LOCALIZED_MESSAGES_CODE = "msg_code";
    String LOCALIZED_MESSAGES_PARAMS = "msg_params";
    String LOCALIZED_MESSAGES_CREATION_TIME = "creation_time";
    String LOCALIZED_MESSAGES_PRIORITY = "priority";
    String LOCALIZED_MESSAGES_GROUP_ORDER = "group_order";
    String LOCALIZED_MESSAGES_GROUP = "message_group";

    // DAO model for RBAC
    String SEQUENCE_ENTITY = "base_entity_seq";
}
