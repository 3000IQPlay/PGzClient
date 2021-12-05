package me.earth.phobos.features.modules.player;

        import me.earth.phobos.features.modules.Module;

        public class AutoKill extends Module {

        public AutoKill() {
                super ( "AutoKill" , "Auto Kill" , Category.PLAYER , true , false , false );
            }

                public AutoKill(String name, String description, Category category, boolean hasListener, boolean hidden, boolean alwaysListening) {
                        super(name, description, category, hasListener, hidden, alwaysListening);
                }

                public void onEnable() {
        mc.player.sendChatMessage("/kill");
        this.toggle();
        }
        //1 line code :)
        }



