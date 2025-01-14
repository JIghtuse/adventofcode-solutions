package machine

enum class MachineState {
    ChoosingAction,
    ChoosingBeverage,
    FillingWater,
    FillingMilk,
    FillingBeans,
    FillingDisposableCups,
    Stopped
}

class Beverage(
    val waterMl: Int,
    val milkMl: Int,
    val beansG: Int,
    val cost: Int
)

val espresso = Beverage(250, 0, 16, 4)
val latte = Beverage(350, 75, 20, 7)
val cappuccino = Beverage(200, 100, 12, 6)

class CoffeeMachine(
    private var waterMl: Int,
    private var milkMl: Int,
    private var beansG: Int,
    private var disposableCups: Int,
    private var money: Int) {

    private var state = MachineState.Stopped

    private val chooseActionMessage = "Write action (buy, fill, take, remaining, exit): "

    fun start() {
        state = MachineState.ChoosingAction
        print(chooseActionMessage)
    }

    fun stopped() = state == MachineState.Stopped

    fun perform(action: String) {
        state = when (state) {
            MachineState.ChoosingAction -> when (action) {
                "buy" -> MachineState.ChoosingBeverage
                "fill" -> MachineState.FillingWater
                "take" -> {
                    takeMoney()
                    MachineState.ChoosingAction
                }
                "remaining" -> {
                    reportState()
                    MachineState.ChoosingAction
                }
                "exit" -> MachineState.Stopped
                else -> {
                    println("Unknown action '$action'")
                    MachineState.ChoosingAction
                }
            }
            MachineState.ChoosingBeverage -> {
                if (action != "back") {
                    buyCoffee(action)
                }
                MachineState.ChoosingAction
            }
            MachineState.FillingWater -> {
                waterMl += action.toInt()
                MachineState.FillingMilk
            }
            MachineState.FillingMilk -> {
                milkMl += action.toInt()
                MachineState.FillingBeans
            }
            MachineState.FillingBeans -> {
                beansG += action.toInt()
                MachineState.FillingDisposableCups
            }
            MachineState.FillingDisposableCups -> {
                disposableCups += action.toInt()
                MachineState.ChoosingAction
            }
            MachineState.Stopped -> MachineState.Stopped
        }

        println()
        print(when (state) {
            MachineState.ChoosingAction -> chooseActionMessage
            MachineState.ChoosingBeverage -> "What do you want to buy? 1 - espresso, 2 - latte, 3 - cappuccino, back - to main menu: "
            MachineState.FillingWater -> "Write how many ml of water do you want to add: "
            MachineState.FillingMilk -> "Write how many ml of milk do you want to add: "
            MachineState.FillingBeans -> "Write how many grams of coffee beans do you want to add: "
            MachineState.FillingDisposableCups -> "Write how many disposable cups of coffee do you want to add: "
            MachineState.Stopped -> ""
        })
    }

    private fun buyCoffee(chosenCoffee: String) {
        when (chosenCoffee.toInt()) {
            1 -> buy(espresso)
            2 -> buy(latte)
            3 -> buy(cappuccino)
            else -> println("Unknown coffee $chosenCoffee")
        }
    }

    private fun takeMoney() {
        println()
        println("I gave you $$money")
        money = 0
    }

    private fun reportState() {
        println()
        println("The coffee machine has:")
        println("$waterMl of water")
        println("$milkMl of milk")
        println("$beansG of coffee beans")
        println("$disposableCups of disposable cups")
        println("$$money of money")
    }

    private fun buy(beverage: Beverage) {
        val enoughWater = waterMl >= beverage.waterMl
        val enoughMilk = milkMl >= beverage.milkMl
        val enoughBeans = beansG >= beverage.beansG
        val enoughIngredients = enoughWater && enoughMilk && enoughBeans

        val hasACup = disposableCups > 0

        if (enoughIngredients && hasACup) {
            println("I have enough resources, making you a coffee!")
            waterMl -= beverage.waterMl
            milkMl -= beverage.milkMl
            beansG -= beverage.beansG
            disposableCups -= 1
            money += beverage.cost
        } else {
            val whatIsMissing = mutableListOf<String>()
            if (!enoughWater) whatIsMissing.add("water")
            if (!enoughBeans) whatIsMissing.add("beans")
            if (!enoughMilk) whatIsMissing.add("milk")
            if (!hasACup) whatIsMissing.add("disposable cups")
            println("Sorry, not enough ${whatIsMissing.joinToString()}!")
        }
    }
}

fun main() {
    val machine = CoffeeMachine(400, 540, 120, 9, 550)

    machine.start()

    while (!machine.stopped()) {
        val action = readLine()!!
        machine.perform(action)
    }
}
