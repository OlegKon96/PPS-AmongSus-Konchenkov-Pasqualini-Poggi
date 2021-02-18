package it.amongsus.core.entities

package object player {
  object Constants {
    final val REPORT_DISTANCE: Int = 5
    final val EMERGENCY_DISTANCE: Int = 2
    object Crewmate {
      final val FIELD_OF_VIEW: Int = 15
      final val NUM_COINS: Int = 0
    }
    object Impostor {
      final val FIELD_OF_VIEW: Int = 20
      final val KILL_DISTANCE: Int = 3
    }
  }
}
