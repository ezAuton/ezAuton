package com.team2502.ezauton.localization;

public class UpdateableGroup implements Updateable
{

    private final Updateable[] updateables;

    public UpdateableGroup(Updateable... updateables)
    {
        this.updateables = updateables;
    }

    /**
     * @return If at least one of the Updateables updated
     */
    @Override
    public boolean update()
    {
        boolean updated = false;
        for(Updateable updateable : updateables)
        {
            if(updateable.update())
            {
                updated = true;
            }
        }
        return updated;
    }
}
