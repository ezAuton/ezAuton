package org.github.ezauton.ezauton.localization;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Describes a group of multiple things that should be updated in unison
 */
//TODO: Suggestion -- Implement List<Updateable>?
public class UpdateableGroup implements Updateable
{

    private final List<Updateable> updateables;

    /**
     * Create a new updateable group
     *
     * @param updateables All the things that should be updated together
     */
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
    //TODO: Perhaps should return a parallel list of which updateables succeeded and which failed, alternatively return true if all succeeded
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
