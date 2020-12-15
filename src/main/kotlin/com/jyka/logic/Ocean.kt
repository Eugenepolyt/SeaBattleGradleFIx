package com.jyka.logic

import com.jyka.view.MainView
import com.jyka.view.WinnerDialog
import javafx.scene.paint.Color
import javafx.scene.image.Image
import javafx.scene.image.ImageView
import javafx.scene.shape.Rectangle
class Ocean(var stack: MutableList<MutableList<Pair<Rectangle, ImageView>>>, private val mW: MainView)  {
    var chosen = false
    private val allShips = mutableSetOf<Ship>()
    var currentShip: Ship? = null

    private val perfectPosition = PerfectPositions(1).mapOfPositions

    private val data = List(10) { MutableList<Ship?>(10) { null } }

    operator fun get(x: Int, y: Int): Ship? = data[x][y]

    operator fun set(x: Int, y: Int, value: Ship?) {
        require(x in 0..9 && y in 0..9)
        data[x][y] = value
        if (value is Ship) {
            value.setBoard(this)
        }
    }

    operator fun set(pair: Pair<Int, Int>, value: Ship?) {
        require(pair.first in 0..9 && pair.second in 0..9)
        data[pair.first][pair.second] = value
        if (value is Ship) {
            value.setBoard(this)
        }
    }
    // u-turn of ship
    fun swap(piece: Ship): MutableList<Pair<Int,Int>>? {
        if (piece.ship.size == 1) return null
        val newList = mutableListOf<Pair<Int, Int>>()
        val firstSq = piece.ship[0]
        if (firstSq.first + piece.ship.size - 1 > 9 || firstSq.second + piece.ship.size - 1 > 9 ) return null

        val check = firstSq.first to firstSq.second + 1 in piece.cantMoveSet
        if (this[firstSq.first,firstSq.second + 1] != null && !check ) {
            for (i in piece.ship.indices) {
                for (sh in allShips) {
                    if (sh == piece) continue
                    if (firstSq.first + i to firstSq.second in sh.cantMoveSet) return null
                }
                newList.add(firstSq.first + i to firstSq.second)
            }
        } else {
            for (i in piece.ship.indices) {
                for (sh in allShips) {
                    if (sh == piece) continue
                    if (firstSq.first to firstSq.second + i in sh.cantMoveSet) return null
                }
                newList.add(firstSq.first to firstSq.second + i)
            }
        }
        return newList
    }

    fun move(rowChange: Int, columnChange: Int) {
        val newList = mutableListOf<Pair<Int, Int>>()
        for (i in currentShip!!.ship.indices) {
            val current = currentShip!!.ship[i]
            newList.add(rowChange + current.first to columnChange + current.second)
        }
        newList.forEach {
            if (it.first > 9 || it.second > 9) return
            if (it.first < 0 || it.second < 0) return
        }
        val backup = currentShip!!.ship
        this.delete(currentShip!!)
        currentShip!!.ship = newList
        currentShip!!.safeRadius()
        if (!isThisItsRadius(currentShip)) {
            currentShip!!.ship = backup
        }

        this.spawn(currentShip!!, 1)
    }
    // Checks if there's a ship nearby
    private fun isThisItsRadius(ship: Ship?): Boolean {
        if (!ship!!.inOcean()) return false

        for (sh in this.allShips) {
            if (sh == ship) continue
            if (ship.ship.last() in sh.cantMoveSet || ship.ship.last() in sh.ship) return  false
            if (ship.ship.first() in sh.cantMoveSet || ship.ship.first() in sh.ship) return  false
        }
        return true
    }

    fun enableHint(ship: Ship) {
        for (i in ship.ship) {
            stack[i.first][i.second].first.apply {
                stroke = Color.GREEN
            }
        }
    }

    fun miss(row: Int, column: Int): Boolean {
        if (stack[row][column].first.fill == Color.rgb(192, 192, 192)) return false
        stack[row][column].first.apply {
            fill = Color.rgb(192, 192, 192)
            stroke = Color.BLACK
        }
        stack[row][column].second.apply {
            image = Image("file:src\\main\\resources\\miss.png")
        }
        return true
    }

    fun inDesk(pair: Pair<Int,Int>): Boolean {
        if(pair.first > 9 || pair.second > 9 || pair.first < 0 || pair.second < 0) return false
        return true
    }

    private fun kill (attackedShip: Ship, player: String): Boolean {
        for(i in attackedShip.ship) {
            stack[i.first][i.second].second.apply {
                image = Image("file:src\\main\\resources\\hit.png")
            }
            stack[i.first][i.second].first.apply {
                stroke = Color.RED
            }
        }
        for (i in attackedShip.cantMoveSet) {
            if(inDesk(i)) {
                miss(i.first, i.second)
            }
        }
        if (checkLooser() && player[0] != '.') {
            WinnerDialog(player, mW).showAndWait()
            return false
        }
        if (checkLooser() && player[0] == '.') {
            mW.restartGame(player)
            return false
        }
        return true
    }

    fun hit(row: Int, column: Int, player: String): Boolean {

        fun closeCell() {
            val closeList = listOf(
                -1 to -1,
                1 to 1,
                -1 to 1,
                1 to -1
            )
            for (i in closeList) {
                if(inDesk(row + i.first to column + i.second)) {
                    miss(row + i.first, column + i.second)
                }
            }
        }

        val attackedShip = this[row,column]!!
        attackedShip.hitCounter++
        if (attackedShip.hitCounter != attackedShip.ship.size) {
            stack[row][column].second.apply {
                image = Image("file:src\\main\\resources\\hit.png")
            }
            stack[row][column].first.stroke = Color.ORANGE

            closeCell()

        } else {
            if(!kill(attackedShip, player)) return false
        }
        return true
    }


    fun disableHint(ship: Ship) {
        for (i in ship.ship) {
            stack[i.first][i.second].first.apply {
                stroke = Color.BLUE
            }
        }
    }

    fun delete (ship: Ship) {
        for (i in ship.ship) {
            this[i] = null
            stack[i.first][i.second].first.apply {
                stroke = Color.DARKGREY
                fill = Color.rgb(255, 255, 255)
            }
        }
    }

    fun spawn(ship: Ship, player: Int) {
        ship.safeRadius()
        for (i in ship.ship) {
            this[i] = ship
            if (player == 1 || player == 3 || player == 4) { // player 3,4 need to bot fight
                stack[i.first][i.second].first.apply {
                    stroke = Color.BLUE
                    fill = Color.rgb(0, 0, 255, .05)
                }
            }
        }
        allShips.add(ship)
    }

    fun spawnAll(player: Int) {

        if (player == 2 || player == 4) {
            val list = perfectPosition[(1..4).random()]!!
            for (position in list) {
                spawn(Ship(position), player)
            }
            setShip(1, player)
            return
        }

        setShip(4, player)
        setShip(3, player)
        setShip(2, player)
        setShip(1, player)

    }

    private fun setShip(size: Int, player: Int) {
        val mapOfSize = mapOf(
            1 to 10,
            2 to 6,
            3 to 3,
            4 to 1

        )
        while (allShips.size != mapOfSize[size]) {
            val shipSpawn: Ship

            val row = (0..10).random()
            val column = (0..10).random()
            val location = (0..2).random()
            shipSpawn = if (location == 1) {
                val shipList = mutableListOf<Pair<Int,Int>>()
                for (i in 0 until size) {
                    shipList.add(row to column + i)
                }
                Ship(shipList)
            } else {
                val shipList = mutableListOf<Pair<Int,Int>>()
                for (i in 0 until size) {
                    shipList.add(row + i to column)
                }
                Ship(shipList)
            }
            if (isThisItsRadius(shipSpawn)) spawn(shipSpawn, player)
        }
    }

    private fun checkLooser(): Boolean {
        for (ship in allShips) {
            if (ship.hitCounter != ship.ship.size) return false
        }
        return true
    }

    // Очищает доску
    fun clear() {
        allShips.clear()
        for (row in 0 until 10) {
            for (column in 0 until 10) {
                this[row,column] = null
                stack[row][column].first.apply {
                    fill = Color.rgb(255, 255, 255)
                    stroke = Color.DARKGREY
                    strokeWidth = 1.0
                }
                stack[row][column].second.apply {
                    image = null
                }
            }
        }
    }

}