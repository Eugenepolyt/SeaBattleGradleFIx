package com.jyka.logic

import com.jyka.view.BotStatistic
import com.jyka.view.MainView
import javafx.scene.paint.Color
import java.io.File

class MotionController(gameBoardList: List<Ocean>, private val mW: MainView) {

    var firstBotStat = 0
    var secondBotStat = 0

    private var gameBoard = gameBoardList[0]
    private var gameBoardBot = gameBoardList[1]
    private var gameBoardSBot = gameBoardList[2]
    private var gameBoardTBot = gameBoardList[3]
    val bot = Bot(gameBoard,  "Bot")
    var statusGame = false
    private var turn = true
    var botFightStatus = true
    var whoFight = 1

    fun startingGame(row: Int, column: Int) {

        if (statusGame) {
            fight(row, column)
            return
        }


        with(gameBoard) {
            //Moving the ship
            if (gameBoard.chosen && gameBoard[row,column] == null) {
                val rowChange = row - currentShip!!.ship[0].first
                val columnChange = column - currentShip!!.ship[0].second
                move(rowChange,columnChange)
                disableHint(gameBoard.currentShip!!)
                gameBoard.currentShip = null
                chosen = false
                return
            }
            // u-turn of the ship
            if (gameBoard[row,column] is Ship) {

                val piece = gameBoard[row, column]
                if (row to column in currentShip?.ship ?: mutableListOf(-1, -1)) { // Click on the ship again
                    val newList = swap(piece!!)
                    if (newList == null) {
                        chosen = false
                        disableHint(currentShip!!)
                        currentShip = null
                        return
                    }
                    delete(piece)
                    piece.ship = newList
                    chosen = false
                    spawn(piece, 1)
                    disableHint(currentShip!!)
                    currentShip = null

                    return
                }
                if (chosen) disableHint(currentShip!!)
                chosen = true
                currentShip = piece!!
                enableHint(currentShip!!)
                return
            }
        }

    }

    private fun fight(row: Int, column: Int) {

        if (whoFight == 1) {
            if (turn) {
                when {
                    gameBoardBot[row, column] is Ship -> gameBoardBot.hit(row, column, "Player")
                    gameBoardBot.stack[row][column].first.stroke == Color.BLACK -> return
                    else -> {
                        gameBoardBot.miss(row, column)
                        turn = false
                        fight(0, 0)
                    }
                }
            } else {
                bot.shot()
                turn = true
            }
        } else { // fight between 2 bots
            val resultList = mutableListOf<Pair<Int,Int>>()
            for (i in 0..15000) {
                botFightStatus = true
                var bot: Bot
                var bot2: Bot
                when {
                    i < 5000 -> {
                        bot = Bot(gameBoardSBot,  ".Bot1")
                        bot.setVersion(1)
                        bot2 = Bot(gameBoardBot,  ".Bot2")
                        bot2.setVersion(2)
                    }
                    i in 5001..10000 -> {
                        bot = Bot(gameBoardTBot,  ".Bot1")
                        bot.setVersion(1)
                        bot2 = Bot(gameBoardBot,  ".Bot2")
                        bot2.setVersion(3)
                    }
                    else -> {
                        bot = Bot(gameBoardTBot,  ".Bot1")
                        bot.setVersion(2)
                        bot2 = Bot(gameBoardBot,  ".Bot2")
                        bot2.setVersion(3)
                    }
                }
                while (botFightStatus) {
                    turn = if (turn) {
                        bot.shot()
                        false
                    } else {
                        bot2.shot()
                        true
                    }
                }
                when (i) {
                    5000, 10000, 15000 -> {
                        resultList.add(firstBotStat to secondBotStat)
                        firstBotStat = 0
                        secondBotStat = 0
                    }
                }
                println(i)
            }
            mW.restartGame("Player")

            val result = """
                Fight:
                
                Version 1: ${resultList[0].first}; Version 2: ${resultList[0].second}
                Version 1: ${resultList[1].first}; Version 3: ${resultList[1].second}
                Version 2: ${resultList[2].first}; Version 3: ${resultList[2].second}      
           
            """.trimIndent()

            File("BotStat.txt").bufferedWriter().use {
                it.newLine()
                it.write(result)
            }
            BotStatistic(result).showAndWait()

        }

    }

}