package com.reactlibrary;

import android.text.TextUtils;

import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.WritableArray;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.bridge.WritableNativeArray;
import com.facebook.react.bridge.WritableNativeMap;
import com.trustingsocial.apisdk.data.TVApiError;
import com.trustingsocial.apisdk.utils.GsonUtils;
import com.trustingsocial.tvsdk.TVDetectionError;
import com.trustingsocial.tvsdk.TVDetectionResult;
import com.trustingsocial.tvsdk.TVIDConfiguration;
import com.trustingsocial.tvsdk.TVSDKConfiguration;
import com.trustingsocial.tvsdk.TVSDKConfiguration.TVActionMode;
import com.trustingsocial.tvsdk.TVSDKConfiguration.TVLivenessMode;
import com.trustingsocial.tvsdk.TVSelfieConfiguration;
import com.trustingsocial.tvsdk.internal.TVCardType;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

class RNTrustVisionUtils {

    static <T> WritableArray toWritableArray(List<T> objects) throws JSONException {
        WritableArray array = new WritableNativeArray();
        for (T element : objects) {
            WritableMap map = convertJsonToMap(new JSONObject(GsonUtils.toJson(element)));
            array.pushMap(map);
        }
        return array;
    }

    static WritableMap convertJsonToMap(JSONObject jsonObject) throws JSONException {
        WritableMap map = new WritableNativeMap();

        Iterator<String> iterator = jsonObject.keys();
        while (iterator.hasNext()) {
            String key = iterator.next();
            Object value = jsonObject.get(key);
            if (value instanceof JSONObject) {
                map.putMap(key, convertJsonToMap((JSONObject) value));
            } else if (value instanceof JSONArray) {
                map.putArray(key, convertJsonToArray((JSONArray) value));
            } else if (value instanceof Boolean) {
                map.putBoolean(key, (Boolean) value);
            } else if (value instanceof Integer) {
                map.putInt(key, (Integer) value);
            } else if (value instanceof Double) {
                map.putDouble(key, (Double) value);
            } else if (value instanceof String) {
                map.putString(key, (String) value);
            } else {
                map.putString(key, value.toString());
            }
        }
        return map;
    }

    static WritableMap objectToMap(Object object) {
        WritableMap map = new WritableNativeMap();
        try {
            return convertJsonToMap(new JSONObject(GsonUtils.toJson(object)));
        } catch (JSONException e) {
            e.printStackTrace();
            return map;
        }
    }

    static WritableArray convertJsonToArray(JSONArray jsonArray) throws JSONException {
        WritableArray array = new WritableNativeArray();

        for (int i = 0; i < jsonArray.length(); i++) {
            Object value = jsonArray.get(i);
            if (value instanceof JSONObject) {
                array.pushMap(convertJsonToMap((JSONObject) value));
            } else if (value instanceof JSONArray) {
                array.pushArray(convertJsonToArray((JSONArray) value));
            } else if (value instanceof Boolean) {
                array.pushBoolean((Boolean) value);
            } else if (value instanceof Integer) {
                array.pushInt((Integer) value);
            } else if (value instanceof Double) {
                array.pushDouble((Double) value);
            } else if (value instanceof String) {
                array.pushString((String) value);
            } else {
                array.pushString(value.toString());
            }
        }
        return array;
    }

    static TVSDKConfiguration convertConfigFromMap(ReadableMap map) {
        TVSDKConfiguration.Builder configuration = new TVSDKConfiguration.Builder();

        if (map.hasKey("isEnableSound")) {
            configuration.setEnableSound(map.getBoolean("isEnableSound"));
        }

        if (map.hasKey("livenessMode")) {
            TVLivenessMode livenessMode = TVLivenessMode.valueOf(map.getString("livenessMode").toUpperCase());
            configuration.setLivenessMode(livenessMode);
        }

        if (map.hasKey("actionMode")) {
            TVActionMode actionMode = TVActionMode.valueOf(map.getString("actionMode").toUpperCase());
            configuration.setActionMode(actionMode);
        }

        if (map.hasKey("cardType")) {
            ReadableMap cardMap = map.getMap("cardType");
            configuration.setCardType(readCardType(cardMap));
        }

        if (map.hasKey("cameraOption")) {
            configuration.setCameraOption(TVSDKConfiguration.TVCameraOption.values()[map.getInt("cameraOption")]);
        }

        if (map.hasKey("isEnableIDSanityCheck")) {
            configuration.setEnableIDSanityCheck(map.getBoolean("isEnableIDSanityCheck"));
        }

        if (map.hasKey("isEnableSelfieSanityCheck")) {
            configuration.setEnableSelfieSanityCheck(map.getBoolean("isEnableSelfieSanityCheck"));
        }

        return configuration.build();
    }

    static TVIDConfiguration convertIdConfigFromMap(ReadableMap map) {
        TVIDConfiguration.Builder configuration = new TVIDConfiguration.Builder();

        if (map.hasKey("isEnableSound")) {
            configuration.setEnableSound(map.getBoolean("isEnableSound"));
        }

        if (map.hasKey("cardType")) {
            ReadableMap cardMap = map.getMap("cardType");
            configuration.setCardType(readCardType(cardMap));
        }

        if (map.hasKey("isEnableSanityCheck")) {
            configuration.setEnableSanityCheck(map.getBoolean("isEnableSanityCheck"));
        }
        return configuration.build();
    }

    private static TVCardType readCardType(ReadableMap readableMap) {
        String cardId = readableMap.getString("cardId");
        String cardName = readableMap.getString("cardName");
        String orientation = readableMap.getString("orientation");
        boolean isRequireBackSide = readableMap.getBoolean("requireBackside");
        return new TVCardType(cardId, cardName, isRequireBackSide, TVCardType.TVCardOrientation.valueOf(orientation));

    }

    static TVSelfieConfiguration convertSelfieConfigFromMap(ReadableMap map) {
        TVSelfieConfiguration.Builder configuration = new TVSelfieConfiguration.Builder();

        if (map.hasKey("isEnableSound")) {
            configuration.setEnableSound(map.getBoolean("isEnableSound"));
        }

        if (map.hasKey("livenessMode")) {
            TVLivenessMode livenessMode = TVLivenessMode.valueOf(map.getString("livenessMode").toUpperCase());
            configuration.setLivenessMode(livenessMode);
        }

        if (map.hasKey("cameraOption")) {
            configuration.setCameraOption(TVSDKConfiguration.TVCameraOption.values()[map.getInt("cameraOption")]);
        }

        if (map.hasKey("isEnableSanityCheck")) {
            configuration.setEnableSanityCheck(map.getBoolean("isEnableSanityCheck"));
        }

        return configuration.build();
    }

    static String convertErrorString(TVDetectionError resultError) {
        String errorCode;
        switch (resultError.getErrorCode()) {
            case TVDetectionError.DETECTION_ERROR_AUTHENTICATION_MISSING:
                errorCode = "authentication_missing_error";
                break;
            case TVDetectionError.DETECTION_ERROR_CAMERA_ERROR:
                errorCode = "camera_error";
                break;
            case TVDetectionError.DETECTION_ERROR_PERMISSION_MISSING:
                errorCode = "permission_missing_error";
                break;
            default:
                errorCode = resultError.getDetailErrorCode();
                break;

        }
        return GsonUtils.toJson(new TVApiError(errorCode, resultError.getErrorDescription()));
    }

    static Map<String, Object> convertResult(TVDetectionResult tvDetectionResult) {
        Map<String, Object> result = new HashMap<>();

        if (tvDetectionResult == null) return result;

        if (tvDetectionResult.getCardType() != null) {
            result.put("cardType", tvDetectionResult.getCardType());
        }

        if (tvDetectionResult.getActionMode() != null) {
            result.put("actionMode", tvDetectionResult.getActionMode().name());
        }

        if (tvDetectionResult.getFaceCompareResult() != null) {
            result.put("compareFaceResult", tvDetectionResult.getFaceCompareResult());
        }

        if (tvDetectionResult.getCardInfoResult() != null) {
            result.put("cardInfoResult", tvDetectionResult.getCardInfoResult());
        }

        if (tvDetectionResult.getLivenessResult() != null) {
            result.put("livenessResult", tvDetectionResult.getLivenessResult());
        }

        if (tvDetectionResult.getIdSanityResult() != null) {
            result.put("idSanityResult", tvDetectionResult.getIdSanityResult());
        }

        if (tvDetectionResult.getSelfieSanityResult() != null) {
            result.put("selfieSanityResult", tvDetectionResult.getSelfieSanityResult());
        }

        if (!TextUtils.isEmpty(tvDetectionResult.getSelfieImageId())) {
            result.put("selfieImageId", tvDetectionResult.getSelfieImageId());
        }

        if (!TextUtils.isEmpty(tvDetectionResult.getIdBackImageId())) {
            result.put("idBackImageId", tvDetectionResult.getIdBackImageId());
        }

        if (!TextUtils.isEmpty(tvDetectionResult.getIdFrontImageId())) {
            result.put("idFrontImageId", tvDetectionResult.getIdFrontImageId());
        }

        if (!TextUtils.isEmpty(tvDetectionResult.getCroppedCardFrontImageId())) {
            result.put("croppedIdFrontImageId", tvDetectionResult.getCroppedCardFrontImageId());
        }

        if (!TextUtils.isEmpty(tvDetectionResult.getCroppedCardBackImageId())) {
            result.put("croppedIdBackImageId", tvDetectionResult.getCroppedCardFrontImageId());
        }

        if (!TextUtils.isEmpty(tvDetectionResult.getSelfieImageUrl())) {
            result.put("selfieImageUrl", tvDetectionResult.getSelfieImageUrl());
        }

        if (!TextUtils.isEmpty(tvDetectionResult.getIdBackImageUrl())) {
            result.put("idBackImageUrl", tvDetectionResult.getIdBackImageId());
        }

        if (!TextUtils.isEmpty(tvDetectionResult.getIdFrontImageUrl())) {
            result.put("idFrontImageUrl", tvDetectionResult.getIdFrontImageId());
        }

        return result;
    }
}