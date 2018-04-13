package nadav.tasher.handasaim.values;

import android.text.InputFilter;
import android.text.Spanned;

public class Filters {
    public static InputFilter codeFilter = new InputFilter() {

        @Override
        public CharSequence filter(CharSequence charSequence, int i, int i1, Spanned spanned, int i2, int i3) {
            if (charSequence != null) {
                for (int c = 0; c < charSequence.length(); c++) {
                    boolean charAllowed = false;
                    String allowed = "0123456789qwertyuiopasdfghjklzxcvbnmQWERTYUIOPASDFGHJKLZXCVBNM";
                    for (int a = 0; a < allowed.length(); a++) {
                        if (charSequence.charAt(c) == allowed.charAt(a)) {
                            charAllowed = true;
                            break;
                        }
                    }
                    if (!charAllowed) return "";
                }
                return null;
            }
            return null;
        }
    };
}
