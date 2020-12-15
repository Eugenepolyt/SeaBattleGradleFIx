package com.jyka.view

import javafx.scene.control.ButtonBar
import javafx.scene.control.ButtonType
import javafx.scene.control.Dialog

class BotStatistic(text: String) : Dialog<ButtonType>() {
    init {
        title = "Congratulation!!!"
        with(dialogPane) {
            headerText = text
            buttonTypes.add(ButtonType("Go next?", ButtonBar.ButtonData.OK_DONE))
        }
    }
}