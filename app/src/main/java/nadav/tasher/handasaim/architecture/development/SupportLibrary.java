package nadav.tasher.handasaim.architecture.development;

import android.app.Activity;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;

import nadav.tasher.jsons.Library;
import nadav.tasher.jsons.Script;

public class SupportLibrary {
    static final Library.Runnable[] limitedSupportCommands = new Library.Runnable[]{
            new Library.Runnable() {
                @Override
                public String getName() {
                    return "getversion";
                }

                @Override
                public String run(ArrayList<String> arrayList) throws Script.ScriptException {
                    return "0";
                }
            }
    };
    static final ArrayList<Library.Runnable> limitedSupportRuns = new ArrayList<>(Arrays.asList(limitedSupportCommands));

    public static final Library limitedSupportLibrary = new Library("limited", limitedSupportRuns);

    public static Library getFullSupport(final Activity a) {
        final Library.Runnable[] commands = new Library.Runnable[]{
                new Library.Runnable() {
                    @Override
                    public String getName() {
                        return "toast";
                    }

                    @Override
                    public String run(ArrayList<String> arrayList) throws Script.ScriptException {
                        if(!arrayList.isEmpty()){
                            Toast.makeText(a,arrayList.get(0),Toast.LENGTH_LONG).show();
                        }else{
                            throw new Script.ScriptException("Can't Toast. No Datas!");
                        }
                        return "ok";
                    }
                }
        };
        final ArrayList<Library.Runnable> runs = new ArrayList<>(Arrays.asList(commands));
        Library l = new Library("handasaim", runs);
        return l;
    }
}
