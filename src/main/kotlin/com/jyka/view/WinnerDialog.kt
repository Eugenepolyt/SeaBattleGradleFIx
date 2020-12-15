package com.jyka.view

import javafx.scene.control.ButtonBar
import javafx.scene.control.ButtonType
import javafx.scene.control.Dialog

class WinnerDialog(winner: String, private val mW: MainView) : Dialog<ButtonType>() {
    init {
        title = "Congratulation!!!"
        with(dialogPane) {
            headerText = if (winner == "Bot") "$winner is win. Don't worry, i believe u will win next time"
            else "$winner is win. Congratulation!!!!!!"
            buttonTypes.add(ButtonType("OK", ButtonBar.ButtonData.OK_DONE))
            mW.restartGame(winner)
        }
    }
}
