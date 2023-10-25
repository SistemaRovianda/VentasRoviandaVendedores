package com.example.ventasrovianda.Utils.bd;
import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.example.ventasrovianda.Utils.bd.daos.ClientDao;
import com.example.ventasrovianda.Utils.bd.daos.ClientVisitDao;
import com.example.ventasrovianda.Utils.bd.daos.DebtDao;
import com.example.ventasrovianda.Utils.bd.daos.DevolutionRequestDao;
import com.example.ventasrovianda.Utils.bd.daos.DevolutionSubSaleDao;
import com.example.ventasrovianda.Utils.bd.daos.EndingDayDao;
import com.example.ventasrovianda.Utils.bd.daos.ProductDao;
import com.example.ventasrovianda.Utils.bd.daos.SaleDao;
import com.example.ventasrovianda.Utils.bd.daos.SubSaleDao;
import com.example.ventasrovianda.Utils.bd.daos.UserDataInitialDao;
import com.example.ventasrovianda.Utils.bd.entities.Client;
import com.example.ventasrovianda.Utils.bd.entities.ClientVisit;
import com.example.ventasrovianda.Utils.bd.entities.Debt;
import com.example.ventasrovianda.Utils.bd.entities.DevolutionRequest;
import com.example.ventasrovianda.Utils.bd.entities.DevolutionSubSale;
import com.example.ventasrovianda.Utils.bd.entities.EndingDay;
import com.example.ventasrovianda.Utils.bd.entities.Product;
import com.example.ventasrovianda.Utils.bd.entities.Sale;
import com.example.ventasrovianda.Utils.bd.entities.SubSale;
import com.example.ventasrovianda.Utils.bd.entities.UserDataInitial;

@Database(entities = {Sale.class, SubSale.class, Client.class, Product.class, UserDataInitial.class, Debt.class, DevolutionRequest.class,DevolutionSubSale.class, EndingDay.class, ClientVisit.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {

    private static AppDatabase instance;
    public abstract SaleDao saleDao();
    public abstract SubSaleDao subSalesDao();
    public abstract ClientDao clientDao();
    public abstract ProductDao productDao();
    public abstract UserDataInitialDao userDataInitialDao();
    public abstract DebtDao debtDao();
    public abstract DevolutionRequestDao devolutionRequestDao();
    public abstract DevolutionSubSaleDao devolutionSubSaleDao();
    public abstract EndingDayDao endingDayDao();
    public abstract ClientVisitDao clientVisitDao();

    public static synchronized AppDatabase getInstance(Context context){
        if(instance==null){
            instance= Room.databaseBuilder(context.getApplicationContext(),AppDatabase.class,"rovisapi").fallbackToDestructiveMigration().build();
        }
        return instance;
    }
}
