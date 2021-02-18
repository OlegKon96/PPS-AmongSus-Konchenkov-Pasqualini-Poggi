package it.amongsus.core.entities.player

import it.amongsus.core.entities.map.Collectionable

trait Crewmate extends Player{
  var numCoins: Int

  def collect(collectionables: Seq[Collectionable], player: Crewmate): Seq[Collectionable] = {
    var newCollectionable: Seq[Collectionable] = collectionables
    collectionables.filter(coin => coin.position == player.position).foreach(coin =>{
      player.numCoins = player.numCoins + 1
      newCollectionable = newCollectionable.filter(c => c != coin)
    })
    newCollectionable
  }
}
