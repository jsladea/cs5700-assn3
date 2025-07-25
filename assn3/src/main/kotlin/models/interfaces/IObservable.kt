package models.interfaces

interface IObservable<T : IObservable<T>> {
    fun addObserver(observer: IObserver<T>)
    fun removeObserver(observer: IObserver<T>)
}