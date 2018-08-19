package ru.taximer.taxiandroid.network.models

///////////////////////////////////////////////////////////////////////////
// Error Model
///////////////////////////////////////////////////////////////////////////

data class ErrorModel(val errMessage: String,
                      val errCode: Int,
                      val errData: String)



class ResponseException(val errorModel: ErrorModel?) : RuntimeException()
{
    override fun getLocalizedMessage(): String  = errorModel?.errMessage ?: "unknown message"
}
