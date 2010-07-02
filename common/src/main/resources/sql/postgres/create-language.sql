-- Workaround to create language plpgsql and avoid error message if it already exist 

-- keep this in one line to avoid trouble with maven plugin
CREATE OR REPLACE FUNCTION public.create_plpgsql_language () RETURNS TEXT AS $$ CREATE LANGUAGE plpgsql; SELECT 'language plpgsql created'::TEXT; $$ LANGUAGE 'sql';

SELECT CASE WHEN
              (SELECT true::BOOLEAN
                 FROM pg_language
                WHERE lanname='plpgsql')
            THEN
              (SELECT 'language already installed'::TEXT)
            ELSE
              (SELECT public.create_plpgsql_language())
            END;

DROP FUNCTION public.create_plpgsql_language ();
