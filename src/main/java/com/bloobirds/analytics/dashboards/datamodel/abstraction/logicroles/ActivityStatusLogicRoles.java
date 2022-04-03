package com.bloobirds.analytics.dashboards.datamodel.abstraction.logicroles;

import com.bloobirds.analytics.dashboards.datamodel.abstraction.LogicRoles;

public enum ActivityStatusLogicRoles implements LogicRoles {
    NONE,
    ACTIVITY__DATA_SOURCE_AUTOMATED,
    ACTIVITY__DATA_SOURCE,
    ACTIVITY__STATUS_TITLE,
    ACTIVITY__TYPE_STATUS_CHANGED_FROM,
    ACTIVITY__TYPE_STATUS_CHANGED_TO,
    ACTIVITY__TYPE_STATUS
}
