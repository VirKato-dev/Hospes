package my.example.hospes;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

/***
 * Адаптер(посредник) между списком данных и видимым списком.
 * Расчитан на вывод данных в зависимости от типа списка людей.
 */
public class RvAdapter extends RecyclerView.Adapter<RvAdapter.ViewHolder> {

    /***
     * ссылка на привязанный список людей
     */
    private ArrayList<Human> data;

    /***
     * Инструмент позволяющий сделать из XML-файла виджет для последующего вывода на экран
     */
    private LayoutInflater inflater;

    /***
     * Интерфейс для передачи сигнала о том, что элемент видимого списка был нажат
     */
    private ItemClickListener clickListener;

    /***
     * Интерфейс для передачи сигнала о том, что на элементе произведён долгий клик
     */
    private ItemLongClickListener longClickListener;

    /***
     * Тип отображаемого списка людей.
     * "o" - сотрудники
     * "g" - посетители
     */
    private String type;


    /***
     * Создать адаптер управляющий выводом данных из списка на экран
     * @param people привязанный список
     */
    public RvAdapter(ArrayList<Human> people, String type) {
        this.type = type;
        setNewList(people);
    }

    /***
     * Привязать список данных для последующего отображения на экране
     * @param people список людей
     */
    public void setNewList(ArrayList<Human> people) {
        data = people;
        notifyDataSetChanged();
    }

    /***
     * Создать виджет в котором будут показаны данные о человеке
     * @param parent контейнер всех отображаемых элементов RecyclerView
     * @param viewType
     * @return
     */
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        inflater = LayoutInflater.from(parent.getContext());
        // по-умолчанию используется форма отображения для посетителей
        View view = inflater.inflate(R.layout.a_guest, parent, false);
        if (type.equals("o")) {
            // для сотрудников используется другая форма отображения данных
            view = inflater.inflate(R.layout.an_operator, parent, false);
        }
        return new ViewHolder(view);
    }

    /***
     * Вывести на экран данные об одном человеке.
     * Зависит от (type) заданного типа списка.
     * @param holder виджет используемый в качестве элемента списка
     * @param position позиция данных в привязанном списке данных
     */
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Human man = getItem(position);
        if (type.equals("g")) {
            // вывод данных о посетителе
            holder.t_man_name.setText(man.name);
            holder.t_room_num.setText("000"); // взять из поля password
            holder.t_room_type.setText("Люкс"); // взять из базы комнат
            holder.t_room_cost.setText("0.00"); // взять из базы комнат
        }
        if (type.equals("o")) {
            // вывод данных о сотруднике
            holder.t_man_name.setText(man.name);
            holder.t_man_role.setText(man.isAdmin ? "Админ" : "Оператор");
        }
    }

    /***
     * @return общее количество данных в списке
     */
    @Override
    public int getItemCount() {
        return data.size();
    }

    /***
     * Получить данные из привязанного списка по номеру позиции
     * @param position позиция в списке
     * @return данные о человеке типа Human
     */
    public Human getItem(int position) {
        return data.get(position);
    }

    /***
     * Подключить обработчик короткого клика по элементу видимого списка
     * @param itemClickListener обработчик клика
     */
    void setClickListener(ItemClickListener itemClickListener) {
        clickListener = itemClickListener;
    }

    /***
     * Обработчик нажатия на элемент списка
     */
    public interface ItemClickListener {
        void onItemClick(View view, int position);
    }

    /***
     * Подключить обработчик долгого клика по элементу видимого списка
     * @param itemLongClickListener обработчик клика
     */
    void setLongClickListener(ItemLongClickListener itemLongClickListener) {
        longClickListener = itemLongClickListener;
    }

    /***
     * Обработчик долгого нажатия на элемент списка
     */
    public interface ItemLongClickListener {
        void onItemClick(View view, int position);
    }

    /***
     * Класс отвечающий за связывание виджетов одного элемента списка.
     * В последующем в каждый из этих виджжетов будут внесены данные
     * из привязанного списка.
     */
    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView t_man_name;
        private TextView t_man_role;
        private TextView t_room_type;
        private TextView t_room_num;
        private TextView t_room_cost;


        private ViewHolder(View itemView) {
            super(itemView);

            // если список сотрудников
            if (type.equals("o")) {
                t_man_role = itemView.findViewById(R.id.t_oper_role);
                t_man_name = itemView.findViewById(R.id.t_oper_name);
            }

            // если список посетителей
            if (type.equals("g")) {
                t_man_name = itemView.findViewById(R.id.t_guest_name);
                t_room_type = itemView.findViewById(R.id.t_room_type);
                t_room_num = itemView.findViewById(R.id.t_room_num);
                t_room_cost = itemView.findViewById(R.id.t_room_cost);
            }

            // привязываем слушатели нажатий к каждому элементу видимого списка

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (clickListener != null) clickListener.onItemClick(v, getAdapterPosition());
                }
            });

            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    if (longClickListener != null) longClickListener.onItemClick(v, getAdapterPosition());
                    return false;
                }
            });
        }
    }

}
