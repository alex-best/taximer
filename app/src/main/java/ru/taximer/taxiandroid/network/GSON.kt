package ru.taximer.taxiandroid.network

import com.google.gson.FieldNamingPolicy
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import ru.taximer.taxiandroid.network.models.ErrorModel
import java.lang.reflect.Type


///////////////////////////////////////////////////////////////////////////
// GSON Config
///////////////////////////////////////////////////////////////////////////

val GSON: Gson by lazy {
    with(GsonBuilder()) {
        serializeNulls()
        setFieldNamingPolicy(FieldNamingPolicy.IDENTITY)
          registerTypeAdapter(ErrorModel::class.java, ErrorModelDeserializer())
    }.create()
}


///////////////////////////////////////////////////////////////////////////
// Error Model Deserializer
///////////////////////////////////////////////////////////////////////////

private class ErrorModelDeserializer : JsonDeserializer<ErrorModel>{

    override fun deserialize(json: JsonElement, typeOfT: Type, context: JsonDeserializationContext): ErrorModel =
            when {
                json.isJsonPrimitive -> {
                    val prim = json.asJsonPrimitive
                    ErrorModel(prim.asString, 0, "")
                }
                json.isJsonObject -> {
                    Gson().fromJson<ErrorModel>(json, ErrorModel::class.java)
                }
                else -> {
                    ErrorModel("Unknown structure for error model", -1, "")
                }
            }
}
