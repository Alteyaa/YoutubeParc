package com.g.youtubeparc.model

import com.google.gson.annotations.SerializedName

data class DetailVideoModel(@SerializedName("kind")
                            val kind: String = "",
                            @SerializedName("pageInfo")
                            val pageInfo: PageInfo,
                            @SerializedName("etag")
                            val etag: String = "",
                            @SerializedName("items")
                            val items: List<ItemsItem>?)