package li.klass.fhem.adapter.devices;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import li.klass.fhem.domain.Device;

public abstract class DeviceListOnlyAdapter<D extends Device> extends DeviceAdapter<D> {

    @Override
    public int getDetailViewLayout() {
        return -1;
    }

    @Override
    public boolean supportsDetailView() {
        return false;
    }

    @Override
    protected View getDeviceDetailView(Context context, LayoutInflater layoutInflater, D device) {
        return null;
    }

    @Override
    protected Intent onFillDeviceDetailIntent(Context context, Device device, Intent intent) {
        return intent;
    }
}
