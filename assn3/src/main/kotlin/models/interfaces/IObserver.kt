package models.interfaces

interface IObserver<T> where T : IObservable<T> {
    fun update(observable: T)
}