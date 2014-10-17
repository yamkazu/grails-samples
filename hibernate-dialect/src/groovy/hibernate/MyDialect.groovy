package hibernate

import org.hibernate.dialect.Dialect
import org.hibernate.dialect.PostgreSQLDialect
import org.hibernate.id.PersistentIdentifierGenerator
import org.hibernate.id.SequenceGenerator
import org.hibernate.type.Type

import java.sql.Types

class MyDialect extends PostgreSQLDialect {

    MyDialect() {
        super()

        // デフォルトカラム長255がきた場合textにマッピングさせ、
        // それ以外はvarchar($l)で長さ指定のvarcharにマッピングさせる
        registerColumnType(Types.VARCHAR, 254, 'varchar($l)')   // 254以下はvarchar
        registerColumnType(Types.VARCHAR, 'varchar($l)')        // 256以上はvarchar
        registerColumnType(Types.VARCHAR, 255, 'text')          // 255はtext
    }

    @Override
    Class<?> getNativeIdentifierGeneratorClass() {
        TableNameSequenceGenerator.class
    }

    static class TableNameSequenceGenerator extends SequenceGenerator {

        @Override
        void configure(Type type, Properties params, Dialect dialect) {
            if (!params.getProperty(SEQUENCE)) {
                String tableName = params.getProperty(PersistentIdentifierGenerator.TABLE)
                if (tableName) {
                    params.setProperty(SEQUENCE, "${tableName}_id_seq")
                }
            }
            super.configure(type, params, dialect)
        }

    }

}
