package it.amongsus

object Constants {
  object Client {
    final val GAME_NAME = "AmongSus"
    final val TURN_TIMER_DURATION = 120
    final val MIN_PLAYERS_NUM: Int = 2
    final val MAX_PLAYERS_NUM: Int = 6
  }
  object Remote {
    final val SERVER_ACTOR_SYSTEM_NAME = "AmongSusServer"
    final val SERVER_LOBBY_ACTOR_NAME = "lobby"
    final val SERVER_ADDRESS = "localhost"//"ec2-15-161-106-30.eu-south-1.compute.amazonaws.com"
    final val SERVER_PORT = 5150
  }
}