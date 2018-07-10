package com.team2502.ezauton.localization;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class UpdateableGroup implements Updateable
{

    private final List<Updateable> updateables;

    public UpdateableGroup(Updateable... updateables)
    {
        this.updateables = new ArrayList<>(Arrays.asList(updateables));
    }

    public void add(Updateable updateable)
    {
        updateables.add(updateable);
    }

    public void remove(Updateable updateable)
    {
        updateables.remove(updateable);
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
