package com.project.gamereviewer.constant;

public final class ApiConstants {
    
    private ApiConstants() {
    }
    
    public static final String API_VERSION = "v1";
    public static final String API_BASE_PATH = "/api/" + API_VERSION;
    
    public static final String GAMES = API_BASE_PATH + "/games";
    public static final String GENRES = API_BASE_PATH + "/genres";
    public static final String PRODUCTION_COMPANIES = API_BASE_PATH + "/production-companies";
    public static final String MEDIA_OUTLETS = API_BASE_PATH + "/media-outlets";
    public static final String REVIEWS = API_BASE_PATH + "/reviews";
    public static final String SYSTEM_REQUIREMENTS = API_BASE_PATH + "/system-requirements";
    public static final String COMPANY_TYPES = API_BASE_PATH + "/company-types";
    public static final String SYSTEM_REQUIREMENT_TYPES = API_BASE_PATH + "/system-requirement-types";
}
