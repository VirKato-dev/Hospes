package my.example.hospes;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

/***
 * Адаптер(посредник) между списком данных и видимым списком
 */
public class Rv_OperatorsAdapter extends RecyclerView.Adapter<Rv_OperatorsAdapter.ViewHolder> {

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
     * Создать адаптер управляющий выводом данных из списка на экран
     * @param people привязанный список
     */
    public Rv_OperatorsAdapter(ArrayList<Human> people) {
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
        View view = inflater.inflate(R.layout.an_operator, parent, false);
        return new ViewHolder(view);
    }

    /***
     * вывести на экран данные об одном человеке
     * @param holder виджет используемый в качестве элемента списка
     * @param position позиция данных в привязанном списке данных
     */
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Human man = getItem(position);
        // данные гостя выводятся иначе, чем данные сотрудника
        if (man.isGuest) {
            holder.t_oper_name.setText(man.name);
            holder.t_oper_role.setText("Гость");
        } else {
            holder.t_oper_name.setText(man.name);
            holder.t_oper_role.setText(man.isAdmin ? "Админ" : "Оператор");
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
     * получить данные из привязанного списка по номеру позиции
     * @param position позиция в списке
     * @return данные о человеке типа Human
     */
    public Human getItem(int position) {
        return data.get(position);
    }

    void setClickListener(ItemClickListener itemClickListener) {
        clickListener = itemClickListener;
    }

    /***
     * Обработчик нажатий на элемент списка
     */
    public interface ItemClickListener {
        void onItemClick(View view, int position);
    }


    /***
     * Класс отвечающий за связывание виджетов одного элемента списка.
     * В последующем в каждый из этих виджжетов будут внесены данные
     * из привязанного списка
     */
    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView t_oper_name;
        private TextView t_oper_role;

        private ViewHolder(View itemView) {
            super(itemView);

            t_oper_name = itemView.findViewById(R.id.t_oper_name);
            t_oper_role = itemView.findViewById(R.id.t_oper_role);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (clickListener != null) clickListener.onItemClick(v, getAdapterPosition());
                }
            });
        }
    }

}
