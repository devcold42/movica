package `in`.devcold.movica.utils

enum class AppMediaType(val mime: String) {
    IMAGE("image/*"),
    IMAGE_JPEG("image/jpeg"),
    IMAGE_PNG("image/png"),
    ZIP("application/zip"),
    PDF("application/pdf")
}