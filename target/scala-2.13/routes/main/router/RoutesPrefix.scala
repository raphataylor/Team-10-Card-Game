// @GENERATOR:play-routes-compiler
// @SOURCE:C:/Users/Ben/Desktop/CARDGAME/Card Game T10/team-10-card-game/conf/routes
// @DATE:Sat Feb 10 11:43:27 GMT 2024


package router {
  object RoutesPrefix {
    private var _prefix: String = "/"
    def setPrefix(p: String): Unit = {
      _prefix = p
    }
    def prefix: String = _prefix
    val byNamePrefix: Function0[String] = { () => prefix }
  }
}
