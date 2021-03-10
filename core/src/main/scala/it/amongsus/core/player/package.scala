package it.amongsus.core

package object player {
  object Constants {
    final val REPORT_DISTANCE: Int = 5
    final val EMERGENCY_DISTANCE: Int = 2
    object Crewmate {
      final val FIELD_OF_VIEW: Int = 6
      final val FIELD_OF_VIEW_SABOTAGE: Int = 3
      final val NUM_COINS: Int = 0
    }
    object CrewmateGhost {
      final val FIELD_OF_VIEW : Int = 100
    }
    object Impostor {
      final val FIELD_OF_VIEW: Int = 10
      final val KILL_DISTANCE: Int = 3
    }
    object ImpostorGhost {
      final val FIELD_OF_VIEW : Int = 1000
    }
  }
}