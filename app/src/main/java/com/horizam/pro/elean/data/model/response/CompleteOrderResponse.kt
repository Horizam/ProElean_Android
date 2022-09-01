package com.horizam.pro.elean.data.model.response

import com.google.gson.annotations.SerializedName

data class CompleteOrderResponse(
    @SerializedName("message" ) var message : String? = null,
    @SerializedName("data"    ) var data    : CompleteResponse?   = CompleteResponse()
)
data class CompleteResponse(

    @SerializedName("id"             ) var id            : String? = null,
    @SerializedName("seller_id"      ) var sellerId      : String? = null,
    @SerializedName("buyer_id"       ) var buyerId       : String? = null,
    @SerializedName("disputed_by"    ) var disputedBy    : String? = null,
    @SerializedName("image"          ) var image         : String? = null,
    @SerializedName("status"         ) var status        : Int?    = null,
    @SerializedName("media"          ) var media         : String? = null,
    @SerializedName("username"       ) var username      : String? = null,
    @SerializedName("s_description"  ) var sDescription  : String? = null,
    @SerializedName("description"    ) var description   : String? = null,
    @SerializedName("service_id"     ) var serviceId     : String? = null,
    @SerializedName("job_id"         ) var jobId         : String? = null,
    @SerializedName("amount"         ) var amount        : Int?    = null,
    @SerializedName("service_rating" ) var serviceRating : Int?    = null,
    @SerializedName("currency"       ) var currency      : String? = null,
    @SerializedName("orderNo"        ) var orderNo       : String? = null,
    @SerializedName("is_rated"       ) var isRated       : Int?    = null,
    @SerializedName("type"           ) var type          : String? = null,
    @SerializedName("watch"          ) var watch         : String? = null,
    @SerializedName("notes"          ) var notes         : String? = null,
    @SerializedName("started"        ) var started       : Int?    = null,
    @SerializedName("created_at"     ) var createdAt     : String? = null,
    @SerializedName("end_date"       ) var endDate       : String? = null,
    @SerializedName("delivery_time"  ) var deliveryTime  : String? = null,
    @SerializedName("delivery_note"  ) var deliveryNote  : String? = null,
    @SerializedName("delivered_file" ) var deliveredFile : String? = null,
    @SerializedName("revision"       ) var revision      : Int?    = null
)

