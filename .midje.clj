(change-defaults  :fact-filter #(and (not (:slow %1))
                                     (not (:acceptance %1))
                                     (not (:danger %1))
                                     )
                  :visible-future false
                 )