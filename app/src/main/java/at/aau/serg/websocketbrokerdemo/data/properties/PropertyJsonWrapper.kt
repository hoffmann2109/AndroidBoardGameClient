package at.aau.serg.websocketbrokerdemo.data.properties

data class PropertyJsonWrapper(
    val properties: List<HouseableProperty>,
    val trainStations: List<TrainStation>,
    val utilities: List<Utility>
)
