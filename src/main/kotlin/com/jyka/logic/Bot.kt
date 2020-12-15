package com.jyka.logic

import com.jyka.view.WinnerDialog
import javafx.scene.paint.Color

class Bot(private var gameBoard: Ocean, private val verison: Int, val name: String) {
    /*
    Several versions of the sea battle bot, knowing the tactics of the game, the game
    is reduced to 5x5 cells and random search for single-deck enemy ships.
    However, some levels of bot complexity are presented here
    -----------------------------------------------------------------------------------------------------------------
    version 1 - random shots
    version 2 - A certain, but not the most optimal algorithm for playing sea battle. Shooting tactics,
    no unnecessary shots
    version 3 - All the same as in the previous version, but it also uses the optimal placement of ships, the most
    optimal shooting at ships...
     */
    var currentShotList = listOf<Pair<Int,Int>>()
    private var attackRow = 0
    private var attackColumn = 0
    private var attacking = false // Was a ship attacked
    var killCheck = 4 // Need to check which ship to look for
    private val stack = gameBoard.stack
    private lateinit var currentShip: Ship // list for finding currentSHip (4,3,2,1)
    private val killMap = mutableMapOf<Int, MutableList<Pair<Int,Int>>>() // map of shots
    private var killedMap = mutableMapOf(
        4 to 0,
        3 to 0,
        2 to 0,
        1 to 0
    ) // Map to check if the ship is dead

    init {

        // Filling in the map of shots
        if (verison == 1) killCheck = 1
        killMap[4] = mutableListOf()
        killMap[3] = mutableListOf()
        killMap[2] = mutableListOf()
        killMap[1] = mutableListOf()
        fillingShotList(verison)

    }


    fun shot (): Boolean {
        currentShotList = if(verison == 1) killMap[1]!!
        else killMap[killCheck]!!

        if (attacking) {
            if(!attack(attackRow,attackColumn)) {
                return false
            }
        }

        for (pair in currentShotList.shuffled()) {
            if (!gameBoard.inDesk(pair)) continue
            if (verison != 1 && !doNeedShot(pair,killCheck)) {
                continue
            }
            if (stack[pair.first][pair.second].first.stroke == Color.BLUE) {
                attackRow = pair.first
                attackColumn = pair.second
                currentShip = gameBoard[attackRow,attackColumn]!!
                if(!attack(attackRow,attackColumn)) {
                    return false
                }
            } else if (stack[pair.first][pair.second].first.stroke == Color.DARKGRAY){
                gameBoard.miss(pair.first,pair.second)
                return false
            }
        }

        shot()
        return false

    }

    // Checks whether to shoot the cage depending on the ship we are looking for
    private fun doNeedShot(pair: Pair<Int,Int>, killCheck: Int):Boolean {

        var yesCounter = 0

        for (i in 1 until killCheck) {
            val newRow = pair.first + i
            val newColumn = pair.second
            if (gameBoard.inDesk(newRow to newColumn)) {
                val stacks = stack[newRow][newColumn].first.stroke
                if (stacks == Color.DARKGRAY || stacks == Color.BLUE) {
                    yesCounter++
                } else break
            }
        }
        for (i in 1 until killCheck) {
            val newRow = pair.first - i
            val newColumn = pair.second
            if (gameBoard.inDesk(newRow to newColumn)) {
                val stacks = stack[newRow][newColumn].first.stroke
                if (stacks == Color.DARKGRAY || stacks == Color.BLUE) {
                    yesCounter++
                } else break
            }
        }
        if (yesCounter >= killCheck - 1) return true

        yesCounter = 0

        for (i in 1 until killCheck) {
            val newRow = pair.first
            val newColumn = pair.second + i
            if (gameBoard.inDesk(newRow to newColumn)) {
                val stacks = stack[newRow][newColumn].first.stroke
                if (stacks == Color.DARKGRAY || stacks == Color.BLUE) {
                    yesCounter++
                } else break
            }
        }

        for (i in 1 until killCheck) {
            val newRow = pair.first
            val newColumn = pair.second - i
            if (gameBoard.inDesk(newRow to newColumn)) {
                val stacks = stack[newRow][newColumn].first.stroke
                if (stacks == Color.DARKGRAY || stacks == Color.BLUE) {
                    yesCounter++
                } else break
            }
        }

        if (yesCounter >= killCheck - 1) return true

        return false
    }

    // function if bot hit in ship
    private fun attack (row: Int, column: Int): Boolean {
        // ship firing list
        val attackingList = listOf(
            0 to 0,
            1 to 0,
            -1 to 0,
            0 to -1,
            0 to 1,
        )
        attacking = true
        // finishing off the ship
        loop@ for (pair in attackingList) {
            var newRow = row
            var newColumn = column
            for (i in 0 until 4) {
                newRow += pair.first
                newColumn += pair.second
                if (!gameBoard.inDesk(newRow to newColumn)) continue
                when(stack[newRow][newColumn].first.stroke) {
                    Color.DARKGREY -> {
                        gameBoard.miss(newRow,newColumn)
                        return false
                    }
                    Color.BLACK  -> continue@loop
                    Color.ORANGE -> continue
                    Color.BLUE -> {
                        if(!gameBoard.hit(newRow,newColumn,name)) return false
                        if (currentShip.hitCounter == currentShip.ship.size) {
                            killedMap[currentShip.ship.size] = killedMap[currentShip.ship.size]!!.plus(1)
                            if (killedMap[4] == 1) killCheck = 3
                            if (killedMap[4] == 1 && killedMap[3] == 2) killCheck = 2
                            if (killedMap[4] == 1 && killedMap[3] == 2 && killedMap[2] == 3) killCheck = 1
                            attacking = false
                            return true
                        }
                    }
                }

            }

        }
        return false
    }

    fun fillingShotList(verison: Int) {

        if (verison == 2 || verison == 1) {
            for (j in 0..8 step 4) {
                for (h in 0..2) {
                    killMap[4]!!.addAll(
                        listOf(
                            j + 0 to 0 + 4 * h,
                            j + 1 to 1 + 4 * h,
                            j + 2 to 3 + 4 * h,
                            j + 3 to 2 + 4 * h,
                        )
                    )
                }
            }
        } else {

            for (j in 3..7 step 4) {
                for (h in 0 until 8) {

                    val newRow = j - h
                    val newColumn = 0 + h
                    if(gameBoard.inDesk(newRow to newColumn)) killMap[4]!!.add(newRow to newColumn)
                }
            }

            for (j in 2..6 step 4) {
                for (h in 0 until 8) {
                    val newRow = 9 - h
                    val newColumn = j + h
                    if(gameBoard.inDesk(newRow to newColumn)) killMap[4]!!.add(newRow to newColumn)
                }
            }
        }

        for (j in 0..9 step 3) {
            for (h in 0..3) {
                killMap[3]!!.addAll(
                    listOf(
                        j + 0 to 1 + 3 * h,
                        j + 1 to 0 + 3 * h,
                        j + 2 to 2 + 3 * h,
                    )
                )
            }
        }

        for (j in 0..8 step 2) {
            for (h in 0..4) {
                killMap[2]!!.addAll(
                    listOf(
                        j + 0 to 0 + 2 * h,
                        j + 1 to 1 + 2 * h,
                    )
                )
            }
        }

        for (i in 0..9) {
            for (j in 0..9) {
                killMap[1]!!.add(i to j)
            }
        }

    }
}