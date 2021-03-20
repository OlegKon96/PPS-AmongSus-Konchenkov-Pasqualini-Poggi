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
      object Strings {
        final val TITLE_MAIN_FRAME: String = "Among Sus - Winner"
        final val TITLE_RESULTS_FRAME: String = "Among Sus - Results"
        final val CREWMATE: String = "Crewmate"
        final val IMPOSTOR: String = "Impostor"
      }
      object Numbers {
        final val WIDTH: Int = 2000
        final val HEIGHT: Int = 800
        final val SPACE_DIMENSION_10: Int = 10
        final val SPACE_DIMENSION_60: Int = 60
      }
    }

    object GameFrame {
      object Numbers {
        final val GAME_FRAME_WIDTH : Int = 1230
        final val GAME_PANEL_WIDTH : Int = 1080
        final val GAME_HEIGHT : Int = 775
        final val BUTTON_PANEL_WIDTH : Int = 150
        final val IMPOSTOR_ROWS_NUMBER : Int = 5
        final val CREWMATE_ROWS_NUMBER : Int = 2
        final val COLS_NUMBER : Int = 1
      }
      object Strings {
        final val TITLE : String = "Among Sus"
        final val REPORT : String = "Report"
        final val KILL : String = "Kill"
        final val VENT : String = "Vent"
        final val EMERGENCY : String = "Call Emergency"
        final val SABOTAGE : String = "Sabotage"
        final val COUNTDOWN : String = "Countdown: "
      }
    }

    object  LobbyFrame {
      object Numbers {
        final val LOBBY_WIDTH: Int = 400
        final val LOBBY_HEIGHT: Int = 300
        final val LOBBY_COLS_NUMBER : Int = 1
        final val ROWS_NUMBER : Int = 4
        final val BASIC_BORDER : Int = 10
        final val RL_BORDER : Int = 120
        final val TB_BORDER : Int = 0
      }
      object Strings {
        final val TITLE : String = "Among Sus"
        final val START_GAME : String = "Start game"
        final val BACK : String = "<"
        final val OF : String = "/"
        final val PLAYERS : String = "Players"
        final val WAIT_PLAYERS : String = "Wait other players"
        final val CODE_LABEL : String = "Your code is : "
        final val WAITING : String = "Waiting..."
      }
    }

    object MenuFrame {
      object Numbers {
        final val BASIC_BORDER : Int = 10
        final val MENU_COLS_NUMBER : Int = 2
        final val ROWS_NUMBER : Int = 4
        final val WIDTH: Int = 600
        final val HEIGHT: Int = 300
        final val VALUES: Seq[Int] = Seq(4,5,6,7,8,9,10)
      }
      object Strings {
        final val TITLE : String = "Among Sus"
        final val INSERT_NAME : String = "Insert your name"
        final val INSERT_NUMBER : String = "Insert the number of players"
        final val JOIN_PUBLIC : String = "Join public game"
        final val CREATE_PRIVATE : String = "Create private game"
        final val INSERT_CODE : String = "Insert code of private game"
        final val JOIN_PRIVATE : String = "Join private game"


      }
    }
  }
}