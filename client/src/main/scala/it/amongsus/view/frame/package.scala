package it.amongsus.view

/**
 * Package to store all constants of all frames
 */
package object frame {
  object Constants {
    object VoteFrame {
      object Strings {
        final val TITLE_MAIN_FRAME: String = "Among Sus - Voting"
        final val TITLE_FRAME_ELIMINATED_PLAYER: String = "Among Sus - Eliminated Player"
        final val TITLE_FRAME_VOTE_PLAYER: String = "Vote Player to Eliminate"
        final val TITLE_FRAME_EXIT_POOL: String = "Among Sus - Exit Pool"
        final val SKIP_VOTE: String = "Skip Vote"
        final val CHAT: String = "Chat"
        final val START_CHATTING: String = "Start Chatting!\n"
        final val SEND_TEXT: String = "Send Text"
        final val CREWMATE: String = "Crewmate"
        final val IMPOSTOR: String = "Impostor"
        final val WAIT_VOTE_OTHER: String = "Wait Vote from Other Players..."
        final val NO_ONE_EJECTED: String = "No One Was Ejected, Parity of Votes..."
      }

      object Numbers {
        final val WIDTH: Int = 1000
        final val HEIGHT: Int = 800
        final val SPACE_DIMENSION_10: Int = 10
        final val SPACE_DIMENSION_20: Int = 10
        final val SPACE_DIMENSION_50: Int = 50
        final val SPACE_DIMENSION_60: Int = 60
        final val SPACE_DIMENSION_65: Int = -65
        final val SPACE_DIMENSION_180: Int = 200
        final val SPACE_DIMENSION_230: Int = 230
        final val GRID_ROW_4: Int = 4
      }
    }

    object WinFrame {

    }

    object GameFrame {}

    object  LobbyFrame {}

    object MenuFrame {}
  }
}