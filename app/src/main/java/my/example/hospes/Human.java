package my.example.hospes;

/***
 * Хаарктеристики пользователя системы.
 * Для хранения всех данных в одном файле
 * используется полиморфизм данных.
 * В зависимости от значения в поле isGuest,
 * значения в полях login и password несут разный смысл и объём.
 */
public class Human {

    /***
     * ФИО человека
     */
    public String name = "";

    /***
     * Для оператора - логин.
     * Для гостя - уникальный идентификатор.
     */
    public String login = "";

    /***
     * Для оператора - пароль.
     * Для гостя - информация о комнате, оплате, сроках заселения
     */
    public String password = "";

    /***
     * Имеет ли оператор право на регистрацию нового оператора
     */
    public boolean isAdmin = false;

    /***
     * Является ли человек гостем/постояльцем
     */
    public boolean isGuest = false;

}
