package my.example.hospes;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Locale;

/***
 * Адаптер(посредник) между списком данных и видимым списком.
 * Расчитан на вывод данных о комнатах.
 */
public class RvRoomsAdapter extends RecyclerView.Adapter<RvRoomsAdapter.ViewHolder> {

    /***
     * Ссылка на привязанный список комнат
     */
    private ArrayList<Room> data;

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
     * Создать адаптер управляющий выводом данных из списка на экран
     * @param rooms привязанный список комнат
     */
    public RvRoomsAdapter(ArrayList<Room> rooms) {
        setNewList(rooms);
    }

    /***
     * Привязать список данных для последующего отображения на экране
     * @param rooms список комнат
     */
    public void setNewList(ArrayList<Room> rooms) {
        data = rooms;
        notifyDataSetChanged();
    }

    /***
     * Создать виджет, в котором будут показаны данные о комнате
     * @param parent контейнер всех отображаемых элементов RecyclerView
     * @param viewType
     * @return
     */
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        inflater = LayoutInflater.from(parent.getContext());
        // по-умолчанию используется форма отображения для посетителей
        View view = inflater.inflate(R.layout.a_room, parent, false);
        return new ViewHolder(view);
    }

    /***
     * Вывести на экран данные об одной комнате.
     * @param holder виджет используемый в качестве элемента списка
     * @param position позиция данных в привязанном списке данных
     */
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Room room = getItem(position);
        // вывод данных о комнате
        holder.t_el_room_type.setText(Room.types.get(room.type));
        holder.t_el_room_num.setText(room.number);
        holder.t_el_room_cost.setText(String.format(Locale.ENGLISH, "%.2f", room.cost));
        holder.t_el_room_desc.setText(room.description);
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
     * @return данные о комнате типа Room
     */
    public Room getItem(int position) {
        return data.get(position);
    }

    /***
     * Обработчик нажатия на элемент списка
     */
    public interface ItemClickListener {
        void onItemClick(View view, int position);
    }

    /***
     * Подключить обработчик короткого клика по элементу видимого списка
     * @param itemClickListener обработчик клика
     */
    void setClickListener(ItemClickListener itemClickListener) {
        clickListener = itemClickListener;
    }

    /***
     * Обработчик долгого нажатия на элемент списка
     */
    public interface ItemLongClickListener {
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
     * Класс отвечающий за связывание виджетов одного элемента списка.
     * В последующем в каждый из этих виджжетов будут внесены данные
     * из привязанного списка.
     */
    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView t_el_room_type;
        private TextView t_el_room_num;
        private TextView t_el_room_cost;
        private TextView t_el_room_desc;


        private ViewHolder(View itemView) {
            super(itemView);

            t_el_room_type = itemView.findViewById(R.id.t_el_room_type);
            t_el_room_num = itemView.findViewById(R.id.t_el_room_num);
            t_el_room_cost = itemView.findViewById(R.id.t_el_room_cost);
            t_el_room_desc = itemView.findViewById(R.id.t_el_room_desc);

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
                    if (longClickListener != null)
                        longClickListener.onItemClick(v, getAdapterPosition());
                    return false;
                }
            });
        }
    }

}
