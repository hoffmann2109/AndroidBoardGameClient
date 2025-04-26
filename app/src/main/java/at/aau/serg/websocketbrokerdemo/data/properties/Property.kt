package at.aau.serg.websocketbrokerdemo.data.properties

sealed class Property {
    abstract val id: Int
    abstract val name: String
    abstract val purchasePrice: Int
    abstract val position: Int
    abstract val image: String
    abstract val isMortgaged: Boolean
}