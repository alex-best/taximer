package ru.taximer.taxiandroid.ui

import android.location.Address
import android.text.TextUtils
import android.util.Log
import io.reactivex.ObservableEmitter
import io.reactivex.ObservableOnSubscribe
import org.json.JSONException
import org.json.JSONObject
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import java.util.Collections
import java.util.Locale


///////////////////////////////////////////////////////////////////////////
// Fallback Reverse Geocoder Observable
///////////////////////////////////////////////////////////////////////////

private const val GOOGLE_API_KEY = "AIzaSyCeNDOCcVI_BNy3SRCT9N13BLs3CWJejFk"

class FallbackReverseGeocoderObservable(private val latitude: Double, private val longitude: Double) : ObservableOnSubscribe<List<Address>> {

    @Throws(Exception::class)
    override fun subscribe(emitter: ObservableEmitter<List<Address>>) {
        try {
            val addresses = alternativeReverseGeocodeQuery()
            if (!emitter.isDisposed) {
                emitter.onNext(addresses)
                emitter.onComplete()
            }
        }
        catch (ex: Exception) {
            if (!emitter.isDisposed) {
                emitter.onError(ex)
            }
        }
    }

    /**
     * This function fetches a list of addresses for the set latitude, longitude and maxResults properties from the
     * Google Geocode API (http://maps.googleapis.com/maps/api/geocode).
     *
     * @return List of addresses
     * @throws IOException In case of network problems
     * @throws JSONException In case of problems while parsing the json response from google geocode API servers
     */
    @Throws(IOException::class, JSONException::class)
    private fun alternativeReverseGeocodeQuery(): List<Address> {
        val url = URL("https://maps.googleapis.com/maps/api/geocode/json?"
                      + "latlng=$latitude,$longitude&sensor=true&language=${Locale.getDefault()}&key=$GOOGLE_API_KEY")
        val urlConnection = url.openConnection() as HttpURLConnection
        val stringBuilder = StringBuilder()
        val outResult = ArrayList<Address>()

        try {
            val reader = BufferedReader(InputStreamReader(urlConnection.inputStream, "UTF-8"))
            var line: String? = reader.readLine()
            while (line != null) {
                stringBuilder.append(line)
                line = reader.readLine()
            }

            // Root json response object
            val jsonRootObject = JSONObject(stringBuilder.toString())

            Log.d("Dto", stringBuilder.toString())

            // No results status
            if ("ZERO_RESULTS".equals(jsonRootObject.getString("status"), true)) {
                return Collections.emptyList()
            }

            // Other non-OK responses status
            if (!"OK".equals(jsonRootObject.getString("status"), true)) {
                throw RuntimeException("Wrong API response")
            }

            // Process results
            val results = jsonRootObject.getJSONArray("results")
            for (i in 0 until results.length()) {
                val address = Address(Locale.getDefault())
                var addressLineString = ""
                val sourceResult = results.getJSONObject(i)
                val addressComponents = sourceResult.getJSONArray("address_components")

                // Assemble address by various components
                for (ac in 0 until addressComponents.length()) {
                    val longNameVal = addressComponents.getJSONObject(ac).getString("long_name")
                    val shortNameVal = addressComponents.getJSONObject(ac).getString("short_name")
                    val acTypes = addressComponents.getJSONObject(ac).getJSONArray("types")
                    val acType = acTypes.getString(0)

                    if (!TextUtils.isEmpty(longNameVal)) {
                        if (acType.equals("street_number", ignoreCase = true)) {
                            if (TextUtils.isEmpty(addressLineString)) {
                                addressLineString = longNameVal
                            }
                            else {
                                addressLineString += " " + longNameVal
                            }
                        }
                        else if (acType.equals("route", ignoreCase = true)) {
                            addressLineString = if (TextUtils.isEmpty(addressLineString)) {
                                longNameVal
                            }
                            else {
                                longNameVal + " " + addressLineString
                            }
                        }
                        else if (acType.equals("sublocality", ignoreCase = true)) {
                            address.subLocality = longNameVal
                        }
                        else if (acType.equals("locality", ignoreCase = true)) {
                            address.locality = longNameVal
                        }
                        else if (acType.equals("administrative_area_level_2", ignoreCase = true)) {
                            address.subAdminArea = longNameVal
                        }
                        else if (acType.equals("administrative_area_level_1", ignoreCase = true)) {
                            address.adminArea = longNameVal
                        }
                        else if (acType.equals("country", ignoreCase = true)) {
                            address.countryName = longNameVal
                            address.countryCode = shortNameVal
                        }
                        else if (acType.equals("postal_code", ignoreCase = true)) {
                            address.postalCode = longNameVal
                        }
                    }
                }

                // Try to get the already formatted address
                val formattedAddress = sourceResult.getString("formatted_address")
                if (!TextUtils.isEmpty(formattedAddress)) {
                    val formattedAddressLines = formattedAddress.split(",".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()

                    for (ia in formattedAddressLines.indices) {
                        address.setAddressLine(ia, formattedAddressLines[ia].trim { it <= ' ' })
                    }
                }
                else if (!TextUtils.isEmpty(addressLineString)) {
                    // If that fails use our manually assembled formatted address
                    address.setAddressLine(0, addressLineString)
                }

                // Finally add address to resulting set
                outResult.add(address)
            }

        }
        finally {
            urlConnection.disconnect()
        }

        return Collections.unmodifiableList(outResult)
    }
}