/**
 * Notification Settings Types
 */

export interface NotificationSettings {
    id: number;
    emailEnabled: boolean;
    emailAddress: string | null;
    lineEnabled: boolean;
    slackEnabled: boolean;
    minPriceThreshold: number | null;
    skillsFilter: string | null;
    remoteOnly: boolean;
    notifyIntervalHours: number;
    lastNotifiedAt: string | null;
}

export interface NotificationSettingsUpdate {
    emailEnabled?: boolean;
    emailAddress?: string;
    lineEnabled?: boolean;
    lineToken?: string;
    slackEnabled?: boolean;
    slackWebhookUrl?: string;
    minPriceThreshold?: number;
    skillsFilter?: string;
    remoteOnly?: boolean;
    notifyIntervalHours?: number;
}
