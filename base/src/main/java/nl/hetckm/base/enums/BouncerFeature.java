package nl.hetckm.base.enums;

public enum BouncerFeature {
    TYPE_UNSPECIFIED(0),
    FACE_DETECTION(1),
    LANDMARK_DETECTION(2),
    LOGO_DETECTION(3),
    LABEL_DETECTION(4),
    TEXT_DETECTION(5),
    DOCUMENT_TEXT_DETECTION(11),
    SAFE_SEARCH_DETECTION(6),
    IMAGE_PROPERTIES(7),
    CROP_HINTS(9),
    WEB_DETECTION(10),
    PRODUCT_SEARCH(12),
    OBJECT_LOCALIZATION(19),
    UNRECOGNIZED(-1);

    public final int value;

    BouncerFeature(Enum<?> value) {
        this.value = valueOf(value.toString()).value;
    }

    BouncerFeature(int value) {
        this.value = value;
    }
}
