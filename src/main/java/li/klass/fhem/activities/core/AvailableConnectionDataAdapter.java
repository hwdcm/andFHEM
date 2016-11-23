/*
 * AndFHEM - Open Source Android application to control a FHEM home automation
 * server.
 *
 * Copyright (c) 2011, Matthias Klass or third-party contributors as
 * indicated by the @author tags or express copyright attribution
 * statements applied by the authors.  All third-party contributions are
 * distributed under license by Red Hat Inc.
 *
 * This copyrighted material is made available to anyone wishing to use, modify,
 * copy, or redistribute it subject to the terms and conditions of the GNU GENERAL PUBLIC LICENSE, as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU GENERAL PUBLIC LICENSE
 * for more details.
 *
 * You should have received a copy of the GNU GENERAL PUBLIC LICENSE
 * along with this distribution; if not, write to:
 *   Free Software Foundation, Inc.
 *   51 Franklin Street, Fifth Floor
 *   Boston, MA  02110-1301  USA
 */

package li.klass.fhem.activities.core;

import android.content.*;
import android.os.*;
import android.util.Log;
import android.view.*;
import android.widget.*;
import com.google.common.collect.ImmutableList;
import li.klass.fhem.R;
import li.klass.fhem.adapter.ListDataAdapter;
import li.klass.fhem.billing.LicenseService;
import li.klass.fhem.constants.*;
import li.klass.fhem.fhem.connection.*;
import li.klass.fhem.fragments.FragmentType;
import li.klass.fhem.service.connection.ConnectionService;
import li.klass.fhem.service.intent.ConnectionsIntentService;
import org.jetbrains.annotations.NotNull;

import java.io.Serializable;
import java.util.*;

import static com.google.common.collect.FluentIterable.from;
import static li.klass.fhem.constants.BundleExtraKeys.*;
import static li.klass.fhem.constants.ResultCodes.SUCCESS;

public class AvailableConnectionDataAdapter extends ListDataAdapter<FHEMServerSpec>
        implements Spinner.OnItemSelectedListener {

    private final Runnable onConnectionChanged;
    private int currentlySelectedPosition = -1;
    private Spinner parent;

    private static class ManagementPill extends FHEMServerSpec {
        private ManagementPill() {
            super(ConnectionService.MANAGEMENT_DATA_ID);
            setName("managementDummy");
        }

        @Override
        public int compareTo(@NotNull FHEMServerSpec fhemServerSpec) {
            return 1;
        }
    }

    private static final ManagementPill MANAGEMENT_PILL = new ManagementPill();

    public AvailableConnectionDataAdapter(Spinner parent, Runnable onConnectionChanged) {
        super(parent.getContext(), R.layout.connection_spinner_item, new ArrayList<>());
        this.onConnectionChanged = onConnectionChanged;
        this.parent = parent;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        FHEMServerSpec server = data.get(position);

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.connection_spinner_item, parent, false);
        }

        TextView nameView = (TextView) convertView.findViewById(R.id.name);
        TextView typeView = (TextView) convertView.findViewById(R.id.type);

        if (server instanceof ManagementPill) {
            nameView.setText(R.string.connectionManage);
            typeView.setVisibility(View.GONE);
        } else {
            nameView.setText(server.getName());
            typeView.setText(server.getServerType().name());
            typeView.setVisibility(View.VISIBLE);
        }

        return convertView;
    }

    @SuppressWarnings("unchecked")
    public void doLoad() {
        context.startService(new Intent(Actions.CONNECTIONS_LIST)
                .setClass(context, ConnectionsIntentService.class)
                .putExtra(BundleExtraKeys.RESULT_RECEIVER, new ResultReceiver(new Handler()) {
            @Override
            protected void onReceiveResult(int resultCode, Bundle resultData) {
                if (resultCode == SUCCESS && resultData != null &&
                        resultData.containsKey(CONNECTION_LIST)) {
                    Serializable content = resultData.getSerializable(CONNECTION_LIST);
                    if (!(content instanceof List)) {
                        throw new IllegalArgumentException(CONNECTION_LIST + " always has to be a list!");
                    }

                    updateData((List<FHEMServerSpec>) content);

                    if (resultData.containsKey(CONNECTION_ID)) {
                        String selectedId = resultData.getString(CONNECTION_ID);
                        select(selectedId);
                    } else {
                        select(ConnectionService.DUMMY_DATA_ID);
                    }
                }
            }
                }));
    }

    private void select(String id) {
        for (int i = 0; i < data.size(); i++) {
            Log.v(AvailableConnectionDataAdapter.class.getName(), data.get(i) + " - " + id);
            if (data.get(i).getId().equals(id)) {
                parent.setSelection(i);
            }
        }
    }

    @Override
    public void updateData(List<FHEMServerSpec> newData) {
        newData.add(MANAGEMENT_PILL);
        super.updateData(filter(newData, parent.getContext()));
    }

    private static List<FHEMServerSpec> filter(List<FHEMServerSpec> data, Context context) {
        ImmutableList<FHEMServerSpec> nonDummies = from(data).filter(i -> !(i instanceof DummyServerSpec)).toList();
        if (!nonDummies.isEmpty() && !LicenseService.isDebug(context)) {
            return nonDummies;
        }
        return data;
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
        if (pos == data.size() - 1) {
            parent.setSelection(currentlySelectedPosition);

            Intent intent = new Intent(Actions.SHOW_FRAGMENT);
            intent.putExtra(BundleExtraKeys.FRAGMENT, FragmentType.CONNECTION_LIST);
            context.sendBroadcast(intent);
        } else {
            currentlySelectedPosition = pos;
            Intent intent = new Intent(Actions.CONNECTION_SET_SELECTED);
            intent.setClass(context, ConnectionsIntentService.class);
            intent.putExtra(BundleExtraKeys.CONNECTION_ID, data.get(pos).getId());
            context.startService(intent);
        }
        onConnectionChanged.run();
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {
        currentlySelectedPosition = -1;
    }
}
