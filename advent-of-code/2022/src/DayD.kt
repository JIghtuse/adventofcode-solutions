package dayd

import java.io.File
import readInput

typealias ValveName = String

data class Valve(val name: ValveName, val flowRate: Int, val directlyConnectedValves: List<String>)

typealias Valves = Map<ValveName, Valve>

val re = """Valve ([A-Z]+) has flow rate=(\d+); tunnels? leads? to valves? ([A-Z, ]+)""".toRegex()

fun String.toValve(): Valve {
    val (name, flowRate, directValves) = re.matchEntire(this)
        ?.destructured
        ?: throw IllegalArgumentException("Unexpected input $this")

    return Valve(name, flowRate.toInt(), directValves.split(", "))
}

fun parseValves(input: List<String>) = input.map(String::toValve).associateBy { it.name }

const val timeLimitMinutes = 30

const val startValve = "AA"

// if we have elephants opening valves in all the rooms at first minute
// answer should be lower
fun maxPossiblePressure(valves: Valves): Int {
    return valves.values.sumOf {
        it.flowRate * (timeLimitMinutes - 1)
    }
}

fun toDotGraph(valves: Valves, outputFileName: String) {
    File(outputFileName).printWriter().use { out ->
        out.println("graph valves {")
        valves.forEach {
            out.println("${it.value.name} [label=\"${it.value.name}: ${it.value.flowRate}\"]")
        }
        valves.forEach { valve ->
            valve.value.directlyConnectedValves.forEach {
                out.println("${valve.value.name} -- $it")
            }
        }
        out.println("}")
    }
}

typealias RoutesToNeighbours = Map<ValveName, List<ValveName>>
typealias ShortestRoutes = Map<ValveName, RoutesToNeighbours>

fun shortestRoutes(valves: Valves, startingValve: ValveName): RoutesToNeighbours {
    val visitedValves = mutableSetOf<ValveName>()
    val routes = mutableMapOf(startingValve to listOf<ValveName>())

    val valvesToLookAt = ArrayDeque<String>()
    valvesToLookAt += startingValve
    while (valvesToLookAt.isNotEmpty()) {
        val valve = valvesToLookAt.removeFirst()
        visitedValves += valve

        valves
            .getValue(valve)
            .directlyConnectedValves
            .filterNot { visitedValves.contains(it) }
            .forEach { neighbour: ValveName ->
                val routeFromCurrentValveToNeighbour = routes.getValue(valve).plus(neighbour)

                if (!routes.contains(neighbour) || routes.getValue(neighbour).size > routeFromCurrentValveToNeighbour.size) {
                    routes[neighbour] = routeFromCurrentValveToNeighbour
                    valvesToLookAt += neighbour
                }
            }
    }

    return routes
}

fun shortestRoutes(valves: Valves): ShortestRoutes {
    return valves.map { it.key to shortestRoutes(valves, it.key) }.toMap()
}


fun mostPressureToRelease(valves: Valves): Int {
    val routes = shortestRoutes(valves)

    val nonZeroValves = valves.filter { it.value.flowRate > 0 }.map { it.key }

    data class MemoItem(val minutesLeft: Int, val location: ValveName, val openedValves: List<ValveName>)

    val memo = mutableMapOf(
        MemoItem(0, startValve, listOf()) to 0,
        MemoItem(1, startValve, listOf()) to 0,
    )
    nonZeroValves.forEach {
        memo[MemoItem(0, it, listOf())] = 0
        memo[MemoItem(1, it, listOf())] = 0
        memo[MemoItem(1, it, listOf(it))] = valves.getValue(it).flowRate
    }

    fun pressure(minutesLeft: Int, location: ValveName, openedValves: List<ValveName>): Int {
        if (minutesLeft <= 0) return 0

        val memoKey = MemoItem(minutesLeft, location, openedValves.sorted())
        val memoizedPressure = memo[memoKey]
        if (memoizedPressure != null) {
            return memoizedPressure
        }

        val alreadyOpenSum = openedValves.sumOf { valves.getValue(it).flowRate }

        if (minutesLeft == 1) {
            memo[memoKey] = alreadyOpenSum
            return alreadyOpenSum
        }

        val currentValveFlow = valves.getValue(location).flowRate

        val valvesToOpen = nonZeroValves.filter {
            !openedValves.contains(it) && routes.getValue(location).getValue(it).size < minutesLeft
        }

        fun pressureWhenMovingTo(valveName: ValveName): Int {
            val moveTime = routes.getValue(location).getValue(valveName).size
            return moveTime * alreadyOpenSum + pressure(minutesLeft - moveTime, valveName, openedValves)
        }

        memo[memoKey] =
            when {
                currentValveFlow > 0 && !openedValves.contains(location) -> {
                    val remainingValvesToOpen = valvesToOpen.minus(location)
                    maxOf(
                        alreadyOpenSum + pressure(minutesLeft - 1, location, openedValves.plus(location)),
                        if (remainingValvesToOpen.isEmpty()) 0 else remainingValvesToOpen.maxOf {
                            pressureWhenMovingTo(it)
                        })
                }
                valvesToOpen.isNotEmpty() ->
                    valvesToOpen.maxOf {
                        pressureWhenMovingTo(it)
                    }
                else -> minutesLeft * alreadyOpenSum
            }

        return memo.getValue(memoKey)
    }

    return pressure(timeLimitMinutes, startValve, listOf())
}

val timeLimitWithElephantMinutes = 26

fun mostPressureToReleaseWithElephant(valves: Valves): Int {
    val routes = shortestRoutes(valves)

    val nonZeroValves = valves.filter { it.value.flowRate > 0 }.map { it.key }

    data class Actor(var location: ValveName, var movingTo: ValveName)

    fun move(actor: Actor): Actor {
        val route = routes.getValue(actor.location).getValue(actor.movingTo)
        return if (route.isEmpty()) actor
        else actor.copy(location = route.first())
    }

    data class MemoItem(
        val minutesLeft: Int,
        val me: Actor,
        val elephant: Actor,
        val openedValves: List<ValveName>)

    val memo = mutableMapOf(
        MemoItem(0, Actor(startValve, startValve), Actor(startValve, startValve), listOf()) to 0,
        MemoItem(1, Actor(startValve, startValve), Actor(startValve, startValve), listOf()) to 0)
    nonZeroValves.forEach {
        memo[MemoItem(0, Actor(it, it), Actor(it, it), listOf())] = 0
        memo[MemoItem(1, Actor(it, it), Actor(it, it), listOf())] = 0
        memo[MemoItem(1, Actor(it, it), Actor(it, it), listOf(it))] = valves.getValue(it).flowRate
    }

    fun pressure(minutesLeft: Int, me: Actor, elephant: Actor, openedValves: List<ValveName>): Int {
        check(minutesLeft >= 0)
//        if (minutesLeft <= 0) return 0

        val memoKey = MemoItem(minutesLeft, me, elephant, openedValves.sorted())
//        if (minutesLeft < 6)
//            println("memo key: $memoKey")
        val memoizedPressure = memo[memoKey]
        if (memoizedPressure != null) {
//            if (minutesLeft < 6)
//                println("returning memoized: $memoizedPressure")
            return memoizedPressure
        }

        fun memoizeAndReturn(p: Int): Int {
            memo[memoKey] = p
            return p
        }

        val alreadyOpenSum = openedValves.sumOf { valves.getValue(it).flowRate }

        if (minutesLeft == 1) {
//            println("last minute, returning already open sum: $alreadyOpenSum")
            return alreadyOpenSum
        }

        val valvesToOpen = nonZeroValves.filter {
            !openedValves.contains(it) &&
                    (routes.getValue(me.location).getValue(it).size < minutesLeft
                            || routes.getValue(elephant.location).getValue(it).size < minutesLeft)
        }

//        if (minutesLeft < 6)
//            println("valves to open: $valvesToOpen; on empty will return ${alreadyOpenSum * minutesLeft}")
        if (valvesToOpen.isEmpty()) return alreadyOpenSum * minutesLeft

//        if (minutesLeft < 8) println("computing pressure")

        val toOpenWithoutMe = valvesToOpen.minus(me.location)
        val toOpenWithoutElephant = valvesToOpen.minus(elephant.location)
        val toOpenWithoutActors = valvesToOpen.minus(me.location).minus(elephant.location)

        val releasedPressure = alreadyOpenSum + when {
            me.location == me.movingTo && elephant.location == elephant.movingTo -> {
//                println("both arrived: me $me elephant $elephant")
//                check(me.location != elephant.location || mineValve.flowRate == 0 && elephantValve.flowRate == 0)
                when {
                    valves.getValue(me.location).flowRate > 0 && valves.getValue(elephant.location).flowRate > 0 -> {
                        if (me.location != elephant.location) {
                            check(!openedValves.contains(me.location))
                            check(!openedValves.contains(elephant.location))
//                            println("both $me != $elephant valves to open: $valvesToOpen; opened: $openedValves")
//                            println("different locations")
                            //                            println("both where to move $whereToMove")
                            if (toOpenWithoutActors.isEmpty()) pressure(minutesLeft - 1, me, elephant,
                                openedValves.plus(me.location).plus(elephant.location))
                            else toOpenWithoutActors.maxOf { firstChoice ->
                                val moreToOpen = toOpenWithoutActors.minus(firstChoice)
//                                println("both more to open $moreToOpen")
                                if (moreToOpen.isEmpty())
                                    pressure(minutesLeft - 1, me.copy(movingTo = firstChoice), elephant.copy(movingTo = firstChoice),
                                        openedValves.plus(me.location).plus(elephant.location))
                                else moreToOpen.maxOf { secondChoice ->
                                    maxOf(
                                        pressure(minutesLeft - 1, me.copy(movingTo = firstChoice), elephant.copy(movingTo = secondChoice),
                                            openedValves.plus(me.location).plus(elephant.location)),
                                        pressure(minutesLeft - 1, me.copy(movingTo = secondChoice), elephant.copy(movingTo = firstChoice),
                                            openedValves.plus(me.location).plus(elephant.location)))
                                }
                            }
                        } else {
//                            println("both $me=$elephant valves to open: $valvesToOpen; opened: $openedValves")
                            check(valvesToOpen.size == 1)
                            check(!openedValves.contains(me.location))
//                            valvesToOpen.maxOf { firstChoice ->
                            pressure(minutesLeft - 1, me, elephant, openedValves.plus(me.location))
//                            }
                        }
                    }
                    // assuming starting position
                    else -> valvesToOpen.maxOf { firstChoice ->
                        val moreToOpen = valvesToOpen.minus(firstChoice)
                        check(moreToOpen.isNotEmpty())
                        moreToOpen.maxOf { secondChoice ->
                            //                            println("[start] second choice is $secondChoice")
                            maxOf(pressure(minutesLeft, me.copy(movingTo = firstChoice), elephant.copy(movingTo = secondChoice), openedValves),
                                pressure(minutesLeft, me.copy(movingTo = secondChoice), elephant.copy(movingTo = firstChoice), openedValves))
                        }
                    }
                }
            }
            me.location == me.movingTo -> {
                val whereToMove = toOpenWithoutMe.minus(elephant.movingTo)
//                println("me arrived: $me; $elephant; opened: $openedValves; toOpen: $valvesToOpen; next move will be")
//                println(if (whereToMove.isEmpty()) elephant.movingTo.toString() else whereToMove.joinToString())
                check(!openedValves.contains(me.location))

                if (whereToMove.isEmpty()) pressure(minutesLeft - 1, me.copy(movingTo = elephant.movingTo), move(elephant), openedValves.plus(me.location))
                else whereToMove.maxOf {
//                    println("me moving to $it now") // what if matches with elephant movingTo?
                    pressure(minutesLeft - 1, me.copy(movingTo = it), move(elephant), openedValves.plus(me.location))
                }
            }
            elephant.location == elephant.movingTo -> {
//                 println("elephant arrived: $elephant; $me; opened: $openedValves; toOpen: $valvesToOpen")
                check(!openedValves.contains(elephant.location))
                val whereToMove = toOpenWithoutElephant.minus(me.movingTo)

                if (whereToMove.isEmpty())
                    pressure(minutesLeft - 1, move(me), elephant.copy(movingTo = me.movingTo), openedValves.plus(elephant.location))
                else whereToMove.maxOf {
//                    println("elephant moving to $it now") // what if matches with me movingTo?
                    pressure(minutesLeft - 1, move(me), elephant.copy(movingTo = it), openedValves.plus(elephant.location))
                }
            }
            else -> pressure(minutesLeft - 1, move(me), move(elephant), openedValves)
        }

//        println("released pressure for $memoKey: $releasedPressure")
        return memoizeAndReturn(releasedPressure)
    }

    return pressure(timeLimitWithElephantMinutes, Actor(startValve, startValve), Actor(startValve, startValve), listOf())
}


fun main() {
    fun part1(valves: Valves, graphName: String): Int {
        println("max possible pressure ${maxPossiblePressure(valves)}")
        return mostPressureToRelease(valves)
    }

    fun part2(valves: Valves): Int {
        println("max possible pressure ${maxPossiblePressure(valves)}")
        return mostPressureToReleaseWithElephant(valves)
    }

    // test if implementation meets criteria from the description, like:
    val testInput = parseValves(readInput("Day16_test"))
    check(part1(testInput, "day16_test.dot") == 1651)
    check(part2(testInput) == 1707)
    println("*** testing done ***")
    println()

    val input = parseValves(readInput("Day16"))
    println(part1(input, "day16.dot"))
    println(part2(input))
}



/*
fun releasedPressure(
    valves: Valves,
    valveOpenOrder: List<String>,
    routes: ShortestRoutes): Int {
    val openedValves = mutableSetOf<ValveName>()
    var pressure = 0

    var currentValve = startValve

    var minute = 1
    for (valveToOpen in valveOpenOrder) {
//        println("[$minute] [pressure] $pressure")
//        println("running to valve $valveToOpen")
//        println("current valve $currentValve, valve to open $valveToOpen")
//        println("routes ")
//        routes.forEach { println(it) }
        val distance = routes[currentValve]!![valveToOpen]!!
//        println("Distance $distance")
        val newMinutes = minOf(minute + distance, timeLimitMinutes)
        val diff = newMinutes - minute
        minute = newMinutes
        currentValve = valveToOpen

        pressure += openedValves.sumOf { diff * valves[it]!!.flowRate }

        if (minute > timeLimitMinutes) break
//        println("opening valve $valveToOpen")
        minute++
        if (minute > timeLimitMinutes) break
        pressure += openedValves.sumOf { valves[it]!!.flowRate }
        openedValves += valveToOpen
    }
    while (minute != timeLimitMinutes + 1) {
        pressure += openedValves.sumOf { valves[it]!!.flowRate }
        ++minute
    }
//    println("minutes left: ${timeLimitMinutes - minute}")

    println("pressure computed: $pressure")
    return pressure
}
*/
/*
fun mostPressureToReleaseWithElephant(valves: Valves): Int {
    val routes = shortestRoutes(valves)

    val nonZeroValves = valves.filter { it.value.flowRate > 0 }.map { it.key }

    data class MemoItem(
        val minutesLeft: Int,
        val mineLocation: ValveName,
        val elephantLocation: ValveName,
        val openedValves: List<ValveName>)

    val memo = mutableMapOf(
        MemoItem(0, startValve, startValve, listOf()) to 0,
        MemoItem(1, startValve, startValve, listOf()) to 0,
        )
    nonZeroValves.forEach {
        memo[MemoItem(0, it, it, listOf())] = 0
        memo[MemoItem(1, it, it, listOf())] = 0
        memo[MemoItem(1, it, it, listOf(it))] = valves.getValue(it).flowRate
    }

    fun pressure(minutesLeft: Int, mineLocation: ValveName, elephantLocation: ValveName, openedValves: List<ValveName>): Int {
        if (minutesLeft <= 0) return 0

        val memoKey = MemoItem(minutesLeft, mineLocation, elephantLocation, openedValves.sorted())
        val memoizedPressure = memo[memoKey]
        if (memoizedPressure != null) {
            return memoizedPressure
        }

        val alreadyOpenSum = openedValves.sumOf { valves.getValue(it).flowRate }

        if (minutesLeft == 1) {
            memo[memoKey] = alreadyOpenSum
            return alreadyOpenSum
        }

        val mineFlow = valves.getValue(mineLocation).flowRate
        val elephantFlow = valves.getValue(mineLocation).flowRate

        val elephantRoutes = routes.getValue(elephantLocation)
        val mineRoutes = routes.getValue(mineLocation)

        val valvesToOpen = nonZeroValves.filter {
            !openedValves.contains(it) &&
            (mineRoutes.getValue(it).size < minutesLeft
             || elephantRoutes.getValue(it).size < minutesLeft)
        }

        fun mineNextPositionTowards(valveName: ValveName) = mineRoutes.getValue(valveName).firstOrNull() ?: mineLocation
        fun elephantNextPositionTowards(valveName: ValveName) = elephantRoutes.getValue(valveName).firstOrNull() ?: elephantLocation

        // makes single step towards given valves
        fun pressureWhenMovingTo(mineValve: ValveName, elephantValve: ValveName): Int {
            return pressure(
                minutesLeft - 1,
                mineNextPositionTowards(mineValve),
                elephantNextPositionTowards(elephantValve),
                openedValves)
        }

        fun moveBoth(firstChoice: ValveName, moreToOpen: List<ValveName>) =
            if (moreToOpen.isEmpty()) {
                pressureWhenMovingTo(firstChoice, firstChoice)
            } else {
                moreToOpen.maxOf { secondChoice ->
                    maxOf(pressureWhenMovingTo(firstChoice, secondChoice),
                          pressureWhenMovingTo(secondChoice, firstChoice))
                }
            }

        memo[memoKey] =
            when {
                mineFlow > 0 && elephantFlow > 0 && !openedValves.contains(mineLocation) && !openedValves.contains(elephantLocation) -> {
                    alreadyOpenSum +
                    if (mineLocation == elephantLocation) {
                        val remainingValvesToOpen = valvesToOpen.minus(mineLocation)
                        if (remainingValvesToOpen.isEmpty())
                            // open current valve and nothing more to do
                            pressure(minutesLeft - 1, mineLocation, elephantLocation, openedValves.plus(mineLocation))
                        else
                            remainingValvesToOpen.maxOf { firstChoice ->
                                val moreToOpen = remainingValvesToOpen.minus(firstChoice)
                                if (moreToOpen.isEmpty())
                                    pressure(minutesLeft - 1, mineLocation, elephantNextPositionTowards(firstChoice), openedValves.plus(mineLocation))
                                else
                                    maxOf(
                                        // I open current valve, elephant goes to one of the remaining valves
                                        pressure(minutesLeft - 1, mineLocation, elephantNextPositionTowards(firstChoice), openedValves.plus(mineLocation)),

                                        // both me and elephant skip current valve and go to one of the remaining valves
                                        // order of choosing valve does not matter, they should just be different
                                        moreToOpen.maxOf { secondChoice ->
                                            pressureWhenMovingTo(firstChoice, secondChoice)
                                        })
                            }
                    } else {
                        // both me and elephant can open different valves or move forward
                        maxOf(
                            // both open valve
                            pressure(minutesLeft - 1, mineLocation, elephantLocation, openedValves.plus(mineLocation).plus(elephantLocation)),
                            // I open valve, elephant moves forward (or we both move somewhere)
                            if (valvesToOpen.minus(mineLocation).isEmpty())
                                pressure(minutesLeft - 1, mineLocation, elephantLocation, openedValves.plus(mineLocation))
                            else valvesToOpen.minus(mineLocation).maxOf { firstChoice ->
                                val moreToOpen = valvesToOpen.minus(mineLocation).minus(firstChoice)
                                maxOf(
                                    pressure(minutesLeft - 1, mineLocation, elephantNextPositionTowards(firstChoice), openedValves.plus(mineLocation)),
                                    moveBoth(firstChoice, moreToOpen)) },

                            // I move forward, elephant opens valve
                            if (valvesToOpen.minus(elephantLocation).isEmpty())
                                pressure(minutesLeft - 1, mineLocation, elephantLocation, openedValves.plus(elephantLocation))
                            else valvesToOpen.minus(elephantLocation).maxOf { firstChoice ->
                                val moreToOpen = valvesToOpen.minus(elephantLocation).minus(firstChoice)

                                maxOf(
                                    pressure(minutesLeft - 1, mineNextPositionTowards(firstChoice), elephantLocation, openedValves.plus(elephantLocation)),
                                    moveBoth(firstChoice, moreToOpen)) },

                            // both move forward
                            if (valvesToOpen.minus(elephantLocation).minus(mineLocation).isEmpty())
                                pressure(minutesLeft - 1, mineLocation, elephantLocation, openedValves.plus(mineLocation).plus(elephantLocation))
                            else valvesToOpen.minus(elephantLocation).minus(mineLocation).maxOf { firstChoice ->
                                val moreToOpen = valvesToOpen.minus(elephantLocation).minus(mineLocation).minus(firstChoice)
                                moveBoth(firstChoice, moreToOpen)
                            })
                    }
                }
                // I open valve, elephant moves forward
                mineFlow > 0 && !openedValves.contains(mineLocation) -> {
                    val remainingValvesToOpen = valvesToOpen.minus(mineLocation)
                    alreadyOpenSum +
                    if (remainingValvesToOpen.isEmpty())
                        pressure(minutesLeft - 1, mineLocation, elephantLocation, openedValves.plus(mineLocation))
                    else remainingValvesToOpen.maxOf { firstChoice ->
                        val moreToOpen = remainingValvesToOpen.minus(firstChoice)
                        maxOf(
                            // opening valve
                            pressure(minutesLeft - 1, mineLocation, elephantNextPositionTowards(firstChoice), openedValves.plus(mineLocation)),
                            moveBoth(firstChoice, moreToOpen))
                    }
                }
                // elephant opens valve, I move forward
                elephantFlow > 0 && !openedValves.contains(elephantLocation) -> {
                    val remainingValvesToOpen = valvesToOpen.minus(elephantLocation)
                    alreadyOpenSum +
                    if (remainingValvesToOpen.isEmpty())
                        pressure(minutesLeft - 1, mineLocation, elephantLocation, openedValves.plus(elephantLocation))
                    else remainingValvesToOpen.maxOf { firstChoice ->
                        val moreToOpen = remainingValvesToOpen.minus(firstChoice)

                        maxOf(
                            // opening valve
                            pressure(minutesLeft - 1, mineNextPositionTowards(firstChoice), elephantLocation, openedValves.plus(elephantLocation)),
                            moveBoth(firstChoice, moreToOpen))
                    }
                }
                // current valves would not be open, we are on our way for the next to open
                valvesToOpen.isNotEmpty() ->
                    alreadyOpenSum + valvesToOpen.maxOf { firstChoice ->
                        moveBoth(firstChoice, valvesToOpen.minus(firstChoice))
                    }
                else -> minutesLeft * alreadyOpenSum
            }

        return memo.getValue(memoKey)
    }

    return pressure(timeLimitWithElephantMinutes, startValve, startValve, listOf())
}
*/
