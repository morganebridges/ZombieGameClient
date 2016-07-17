/*ackage com.fourninenine.zombiegameclient.services;

import android.app.Activity;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import com.fourninenine.zombiegameclient.MainMapActivity;
import com.fourninenine.zombiegameclient.httpServices.RESTServices.HttpUserService;
import com.fourninenine.zombiegameclient.models.User;
import com.fourninenine.zombiegameclient.models.Zombie;
import com.fourninenine.zombiegameclient.models.dto.ClientUpdateDto;
import com.fourninenine.zombiegameclient.models.dto.UserActionDto;
import com.fourninenine.zombiegameclient.models.utilities.ApplicationContextProvider;
import com.fourninenine.zombiegameclient.services.activityHelpers.CollectionProcessing;

import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UpdateRetryService {
    Activity caller;
    UserActionDto
    public UpdateRetryService(Activity caller, UserActionDto dto) {
        HttpUserService service = new HttpUserService();

    }
    @Override
    public void onCreate(){
        try {
            Call<ClientUpdateDto> retryCall = service.update(dto);
            retryCall.enqueue(new Callback<ClientUpdateDto>() {
                @Override
                public void onResponse(Call<ClientUpdateDto> call, Response<ClientUpdateDto> response) {
                    ClientUpdateDto dto = response.body();
                    if(dto.getZombies() == null){
                        Intent retryService = new Intent(this, dto);
                    }
                    HashMap<Long, Zombie> zombies = CollectionProcessing.zombieListToMap(dto.getZombies());
                    User user = dto.getUser();
                    User.save(user);
                    onBind(new Intent())
                }

                @Override
                public void onFailure(Call<ClientUpdateDto> call, Throwable t) {
                    System.out.println("WHAT HAPPENEND????");
                }
            }););
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        Intent mapIntent = new Intent(ApplicationContextProvider.getAppContext(), MainMapActivity.class);
        mapIntent.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
*/