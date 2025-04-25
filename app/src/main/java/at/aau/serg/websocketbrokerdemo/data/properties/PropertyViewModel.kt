package at.aau.serg.websocketbrokerdemo.data.properties

import android.content.Context
import androidx.lifecycle.ViewModel
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import at.aau.serg.websocketbrokerdemo.data.properties.Property
import at.aau.serg.websocketbrokerdemo.data.properties.HouseableProperty
import at.aau.serg.websocketbrokerdemo.data.properties.TrainStation
import at.aau.serg.websocketbrokerdemo.data.properties.Utility
import at.aau.serg.websocketbrokerdemo.data.properties.loadJsonFromAssets

class PropertyViewModel : ViewModel() {

    fun getProperties(context: Context): List<Property> {
        val json = loadJsonFromAssets(context, "propertyData.json") // Load JSON from assets
        val gson = Gson()

        // Define TypeToken for parsing
        val propertyListType = object : TypeToken<List<Map<String, Any>>>() {}.type

        val propertiesJsonList: List<Map<String, Any>> = gson.fromJson(json, propertyListType)

        // Parse each property type
        val properties = mutableListOf<Property>()

        propertiesJsonList.forEach { propertyJson ->
            val type = propertyJson["type"] as? String

            when (type) {
                "property" -> {
                    properties.add(
                        gson.fromJson(gson.toJson(propertyJson), HouseableProperty::class.java)
                    )
                }
                "trainStation" -> {
                    properties.add(
                        gson.fromJson(gson.toJson(propertyJson), TrainStation::class.java)
                    )
                }
                "utility" -> {
                    properties.add(
                        gson.fromJson(gson.toJson(propertyJson), Utility::class.java)
                    )
                }
            }
        }
        return properties
    }
}