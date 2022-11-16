package com.petter.konachan.network

/**
 * @anthor: EDZ
 * @time: 2021/11/16 16:49
 * @description:
 */
enum class UrlEnum(val code: Int, val baseUrl: String, val post: String, val tags: String) {

    KONACHAN_CHILDREN(1, "http://konachan.net/", "post.json", "tag.json"),
    KONACHAN_MAN(2, "http://konachan.com/", "post.json", "tag.json"),
    GELBOORU(
        3,
        "https://gelbooru.com/",
        "index.php?page=dapi&s=post&q=index&json=1&limit=14",
        "index.php?page=dapi&s=tag&q=index&json=1&limit=10"
    )

}