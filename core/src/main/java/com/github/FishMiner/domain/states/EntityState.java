package com.github.FishMiner.domain.states;

public class EntityState {
    public enum HookStates implements IState {
        SWINGING {
            @Override
            public void onEnter() { System.out.println("Hook is swinging and ready to fire."); }
            @Override
            public void onExit() { System.out.println("Hook has been fired and stops swinging."); }
        },
        FIRE {
            @Override
            public void onEnter() { System.out.println("Hook is fired towards the target."); }
            @Override
            public void onExit() { System.out.println("Hook reached target or stopped."); }
        },
        REELING {
            @Override
            public void onEnter() { System.out.println("Hook is returning to the original position."); }
            @Override
            public void onExit() { System.out.println("Hook is back at the starting point."); }
        };

        @Override
        public IState getState(int i) {
            if (i >= 0 && i < HookStates.values().length) {
                return HookStates.values()[i];
            }
            return null;
        }

        @Override
        public String getAnimationKey() {
            return this.name().toLowerCase();
        }
    }


    public enum FishStates implements IState {
        FISHABLE {
            @Override
            public void onEnter() {
                System.out.println("Fish is now available for fishing.");
                // Add logic for animation changes or event triggers
            }

            @Override
            public void onExit() {
                System.out.println("Fish is no longer freely swimming.");
            }
        },
        HOOKED {
            @Override
            public void onEnter() {
                System.out.println("Fish is hooked! Struggle animation starts.");
            }

            @Override
            public void onExit() {
                System.out.println("Fish is either captured or escaped.");
            }
        },
        REELING {
            @Override
            public void onEnter() {
                System.out.println("Fish is being reeled in.");
            }

            @Override
            public void onExit() {
                System.out.println("Fish has been reeled in.");
            }
        },
        CAPTURED {
            @Override
            public void onEnter() {
                System.out.println("Fish is captured and added to inventory.");
            }

            @Override
            public void onExit() {
                System.out.println("Fish is removed from water.");
            }
        },
        ATTACKS {
            @Override
            public void onEnter() {
                System.out.println("Fish is attacking! Triggering fight sequence.");
            }

            @Override
            public void onExit() {
                System.out.println("Fish attack ends.");
            }

            @Override
            public String getAnimationKey() {
                return "";
            }
        };

        @Override
        public IState getState(int i) {
            if (i >= 0 && i < FishStates.values().length) {
                return FishStates.values()[i];
            }
            return null;
        }

        @Override
        public String getAnimationKey() {
            return this.name().toLowerCase();
        }
    }
}
