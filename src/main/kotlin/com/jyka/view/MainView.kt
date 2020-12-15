package com.jyka.view

import com.jyka.logic.MotionController
import com.jyka.logic.Ocean
import javafx.scene.control.Button
import javafx.scene.image.ImageView
import javafx.scene.layout.BorderPane
import javafx.scene.paint.Color
import javafx.scene.shape.Rectangle
import tornadofx.*


class MainView : View("BattleShip") {


    override val root = BorderPane()

    private val stack = MutableList(10) { MutableList(10) { Rectangle() to ImageView() } }
    private val stackBot = MutableList(10) { MutableList(10) { Rectangle() to ImageView() } }
    private val stackSecondBot = MutableList(10) { MutableList(10) { Rectangle() to ImageView() } }
    private val stackThirdBot = MutableList(10) { MutableList(10) { Rectangle() to ImageView() } }
    private var botVersion = 1
    private var spawnPos = 0
    private val gameBoard = Ocean(stack, this)
    private val gameBoardBot = Ocean(stackBot, this)
    private val gameBoardSecondBot = Ocean(stackSecondBot, this)
    private val gameBoardThirdBot = Ocean(stackThirdBot, this)
    private val gameBoardList = listOf(gameBoard,gameBoardBot,gameBoardSecondBot,gameBoardThirdBot)
    private var controller = MotionController(gameBoardList,botVersion, this)
    private var button = Button()

    init {
        primaryStage.isResizable = false

        with(root) {
            top {
                borderpane {

                    top {
                        vbox {
                            menubar {
                                menu("Game") {
                                    item("Restart").action {
                                    }
                                    separator()
                                    item("Bot fight").action {
                                        gameBoard.clear()
                                        gameBoardBot.clear()
                                        gameBoardSecondBot.clear()
                                        controller.statusGame = true
                                        gameBoardBot.spawnAll(3)
                                        gameBoardSecondBot.spawnAll(3)
                                        gameBoardThirdBot.spawnAll(4)
                                        controller.whoFight = 0
                                        controller.startingGame(1,1)
                                    }
                                    separator()
                                    item("Exit").action {
                                        this@MainView.close()
                                    }

                                }
                                menu("Bot version") {
                                    item("Easy").action {
                                        if (!controller.statusGame) {
                                            botVersion = 1
                                            spawnPos = 0
                                        }

                                    }
                                    separator()
                                    item("Medium").action {
                                        if (!controller.statusGame) {
                                            botVersion = 2
                                            spawnPos = 0
                                        }
                                    }
                                    separator()
                                    item("Hard").action {
                                        if (!controller.statusGame) {
                                            botVersion = 3
                                            spawnPos = 2
                                        }
                                    }
                                }
                            }
                        }
                    }
                    center {
                        button = button("Start Game") {
                            action {
                                if (!controller.statusGame) {
                                    text = "Restart game"
                                    gameBoardBot.spawnAll(spawnPos)
                                    controller.statusGame = true
                                } else {
                                    text = "Start game"
                                    restartGame("Player")
                                }

                            }
                        }
                    }
                }
            }
            center {
                gridpane {
                    paddingLeft = 20
                    paddingBottom = 20
                    paddingTop = 20
                    for (row in 0..9) {
                        row {
                            for (column in 0..9) {
                                stackpane {
                                    val rectangle = rectangle {
                                        fill = Color.rgb(255, 255, 255)
                                        stroke = Color.DARKGREY
                                        strokeWidth = 1.0
                                        width = 50.0
                                        height = 50.0
                                    }

                                    val image = imageview {
                                        image = null
                                    }

                                    stack[row][column] = rectangle to image

                                    setOnMouseClicked{
                                        if (!controller.statusGame) controller.startingGame(row,column)
                                    }

                                }
                            }
                        }
                    }
                }
            }
            right {
                gridpane {
                    paddingLeft = 50
                    paddingBottom = 20
                    paddingTop = 20
                    paddingRight = 20
                    for (row in 0..9) {
                        row {
                            for (column in 0..9) {
                                stackpane {
                                    val rectangle = rectangle {
                                        fill = Color.rgb(255, 255, 255)
                                        stroke = Color.DARKGREY
                                        strokeWidth = 1.0
                                        width = 50.0
                                        height = 50.0
                                    }

                                    val image = imageview {
                                        image = null
                                    }

                                    stackBot[row][column] = rectangle to image
                                    setOnMouseClicked {
                                        if (controller.statusGame) controller.startingGame(row, column)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        for (i in 0..9) {
            for (j in 0..9) {
                val rectangle = Rectangle(50.0,50.0).apply {
                    fill = Color.rgb(255, 255, 255)
                    stroke = Color.DARKGREY
                    strokeWidth = 1.0
                }

                val image = imageview {
                    image = null
                }

                stackSecondBot[i][j] = rectangle to image
            }
        }

        for (i in 0..9) {
            for (j in 0..9) {
                val rectangle = Rectangle(50.0,50.0).apply {
                    fill = Color.rgb(255, 255, 255)
                    stroke = Color.DARKGREY
                    strokeWidth = 1.0
                }

                val image = imageview {
                    image = null
                }

                stackSecondBot[i][j] = rectangle to image
            }
        }


        gameBoard.spawnAll(1)
    }
    fun restartGame (name: String) {
        if (name[0] == '.') {
            when(name.last()) {
                '1' -> controller.firstBotStat++
                '2' -> controller.secondBotStat++
            }
            controller.botFightStatus = false
            gameBoardBot.clear()
            gameBoardSecondBot.clear()
            gameBoardThirdBot.clear()
            gameBoardBot.spawnAll(3)
            gameBoardSecondBot.spawnAll(3)
            gameBoardThirdBot.spawnAll(4)

        } else {
            button.apply {
                text = "Start Game"
            }
            gameBoard.clear()
            gameBoardBot.clear()
            gameBoardSecondBot.clear()
            gameBoardThirdBot.clear()
            controller.statusGame = false
            controller.whoFight = 1
            gameBoard.spawnAll(1)
            controller = MotionController(gameBoardList, botVersion,this)
        }
    }
}