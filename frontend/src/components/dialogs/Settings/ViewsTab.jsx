import React, { forwardRef } from 'react';
import MaterialTable from 'material-table';
import AddBox from '@material-ui/icons/AddBox';
import ArrowUpward from '@material-ui/icons/ArrowUpward';
import Check from '@material-ui/icons/Check';
import ChevronLeft from '@material-ui/icons/ChevronLeft';
import ChevronRight from '@material-ui/icons/ChevronRight';
import Clear from '@material-ui/icons/Clear';
import DeleteOutline from '@material-ui/icons/DeleteOutline';
import Edit from '@material-ui/icons/Edit';
import FilterList from '@material-ui/icons/FilterList';
import FirstPage from '@material-ui/icons/FirstPage';
import LastPage from '@material-ui/icons/LastPage';
import Remove from '@material-ui/icons/Remove';
import Refresh from '@material-ui/icons/Refresh';
import ViewColumn from '@material-ui/icons/ViewColumn';
import SaveAlt from '@material-ui/icons/SaveAlt';
import Search from '@material-ui/icons/Search';
import Cookies from 'universal-cookie';
import ColumnsLookup from './ColumnsLookup';
import FiltersLookup from './FiltersLookup';

const cookies = new Cookies();
const BUCKET_DEFAULT = 'every';
const CLASS_DEFAULT = 'none';
const FILTER_DEFAULT = 'none';

const tableIcons = {
    Add: forwardRef((props, ref) => <AddBox {...props} ref={ref} />),
    Check: forwardRef((props, ref) => <Check {...props} ref={ref} />),
    Clear: forwardRef((props, ref) => <Clear {...props} ref={ref} />),
    Delete: forwardRef((props, ref) => <DeleteOutline {...props} ref={ref} />),
    DetailPanel: forwardRef((props, ref) => <ChevronRight {...props} ref={ref} />),
    Edit: forwardRef((props, ref) => <Edit {...props} ref={ref} />),
    Export: forwardRef((props, ref) => <SaveAlt {...props} ref={ref} />),
    Filter: forwardRef((props, ref) => <FilterList {...props} ref={ref} />),
    FirstPage: forwardRef((props, ref) => <FirstPage {...props} ref={ref} />),
    LastPage: forwardRef((props, ref) => <LastPage {...props} ref={ref} />),
    NextPage: forwardRef((props, ref) => <ChevronRight {...props} ref={ref} />),
    PreviousPage: forwardRef((props, ref) => <ChevronLeft {...props} ref={ref} />),
    ResetSearch: forwardRef((props, ref) => <Clear {...props} ref={ref} />),
    Search: forwardRef((props, ref) => <Search {...props} ref={ref} />),
    SortArrow: forwardRef((props, ref) => <ArrowUpward {...props} ref={ref} />),
    ThirdStateCheck: forwardRef((props, ref) => <Remove {...props} ref={ref} />),
    ViewColumn: forwardRef((props, ref) => <ViewColumn {...props} ref={ref} />),
};

function handleErrors(res) {
    if (res.ok) {
        return res.json();
    } else {
        return res.json().then(err => { throw err; });
    }
}

class ViewsTab extends React.Component {

    constructor(props) {
        super(props);
        this.tableRef = React.createRef();
        this.pageSize = this.getLastPageSize();
        this.allColumns = props.columns;
        this.allFilters = props.filters;
        this.state = {
            columns: [
                { title: 'Id', field: 'view_id', type: 'numeric', editable: 'never', filtering: false },
                { title: 'Name', field: 'view_name' },
                { title: 'Bucket', field: 'bucket_id', initialEditValue: BUCKET_DEFAULT, emptyValue: BUCKET_DEFAULT, lookup: props.bucketsLookup },
                { title: 'Class', field: 'class_id', initialEditValue: CLASS_DEFAULT, emptyValue: CLASS_DEFAULT, lookup: props.classesLookup },
                { title: 'Description', field: 'description' },
                {
                    title: 'Columns', field: 'columns_id', type: 'numeric', lookup: this.getColumnsLookup(props.columns), initialEditValue: 0,
                    editComponent: props => <ColumnsLookup rowData={props.rowData} allColumns={this.allColumns} onChange={props.onChange} />
                },
                {
                    title: 'Filter', field: 'filter_id', type: 'numeric', lookup: this.getFiltersLookup(props.filters), initialEditValue: FILTER_DEFAULT, emptyValue: FILTER_DEFAULT,
                    editComponent: props => <FiltersLookup rowData={props.rowData} allFilters={this.allFilters} onChange={props.onChange} />
                },
                {
                    title: 'Created at', field: 'created_at', type: 'datetime', editable: 'never', filtering: false,
                    render: rowData => <div>{rowData != null ? rowData.created_at != null ? new Date(rowData.created_at).toLocaleString() : null : null}</div>,
                },
                { title: 'Created by', field: 'created_by', editable: 'never' },
                {
                    title: 'Updated at', field: 'updated_at', type: 'datetime', editable: 'never', filtering: false,
                    render: rowData => <div>{rowData != null ? rowData.updated_at != null ? new Date(rowData.updated_at).toLocaleString() : null : null}</div>,
                },
                { title: 'Updated by', field: 'updated_by', editable: 'never' },

            ],
            filtering: false
        };
    }

    getColumnsLookup(columns) {
        let lookup = {};
        columns.map((column) => (lookup[column.columns_id] = column.columns_name));
        return lookup;
    }

    getFiltersLookup(filters) {
        let lookup = {};
        lookup[FILTER_DEFAULT] = '- none -';
        filters.map((filter) => (lookup[filter.filter_id.toString()] = filter.filter_name));
        return lookup;
    }

    getLastPageSize() {
        const lastPageSize = cookies.get('last_page_size');
        if (lastPageSize != null) {
            return parseInt(lastPageSize);
        } else
            return 15;
    }

    setLastPageSize(pageSize) {
        const current = new Date();
        const nextYear = new Date();
        nextYear.setFullYear(current.getFullYear() + 1);
        cookies.set('last_page_size', pageSize, { path: window.location.href, expires: nextYear });
    }

    convertNullsToNoneOrEvery(inputData) {
        for (var i = 0; i < inputData.length; i++) {
            let item = inputData[i];
            
            if (item.class_id == null)
                item.class_id = CLASS_DEFAULT;
            else
                item.class_id = item.class_id.toString();

            if (item.bucket_id == null)
                item.bucket_id = BUCKET_DEFAULT;
            else
                item.bucket_id = item.bucket_id.toString();

            if (item.filter_id == null)
                item.filter_id = FILTER_DEFAULT;
            else
                item.filter_id = item.filter_id.toString();

            if (item.description == null)
                item.description = '';
        }
        return inputData;
    }

    render() {
        return (
            <MaterialTable
                icons={tableIcons}
                title='Views'
                tableRef={this.tableRef}
                columns={this.state.columns}
                data={query =>
                    new Promise((resolve, reject) => {
                        if (this.pageSize !== query.pageSize) {
                            this.pageSize = query.pageSize;
                            this.setLastPageSize(query.pageSize);
                        }

                        let url = window.API + '/views?';
                        url += 'limit=' + query.pageSize;
                        url += '&page=' + (query.page + 1);

                        if (query.orderBy != null && query.orderBy.field != null)
                            if (query.orderDirection === 'desc')
                                url += '&sort=desc(' + query.orderBy.field + ')';
                            else
                                url += '&sort=' + query.orderBy.field;

                        if (this.state.filtering && query.filters.length > 0) {
                            let filterUrl = '&filter=';
                            let addFilter = false;
                            for (const filter of query.filters)
                                if (filter.value.length > 0) {
                                    addFilter = true;
                                    if (filter.column.field === 'bucket_id')
                                        filterUrl += '(' + filter.column.field + ';in;numeric_array;' + filter.value + ')';
                                    else if (filter.column.type === 'numeric')
                                        filterUrl += '(' + filter.column.field + ';' + filter.operator + ';numeric;' + filter.value + ')';
                                    else if (filter.column.type === 'boolean')
                                        filterUrl += '(' + filter.column.field + ';=;boolean;' + (filter.value === 'checked') + ')';
                                    else
                                        filterUrl += escape('(' + filter.column.field + ';like;text;%' + filter.value + '%)');
                                }
                            if (addFilter)
                                url += filterUrl;
                        }

                        fetch(url)
                            .then(response => response.json())
                            .then(result => {
                                resolve({
                                    data: this.convertNullsToNoneOrEvery(result.views),
                                    page: result.page - 1,
                                    totalCount: result.total,
                                })
                            });
                    })
                }
                options={{
                    pageSize: this.pageSize,
                    pageSizeOptions: [15, 20, 25, 30, 35, 40, 45, 50],
                    paginationType: 'stepped',
                    actionsColumnIndex: -1,
                    sorting: true,
                    search: false,
                    filtering: this.state.filtering,
                    debounceInterval: 700,
                    padding: 'dense',
                    headerStyle:{backgroundColor:'#eeeeee'},
                    rowStyle: rowData => ({ backgroundColor: rowData.tableData.id % 2 === 1 ? '#fafafa' : '#FFF' })
                }}
                components={{
                    Container: props => <div {...props} />
                }}
                actions={[
                    {
                        icon: () => <Refresh />,
                        tooltip: 'Refresh',
                        isFreeAction: true,
                        onClick: () => { this.tableRef.current !== null && this.tableRef.current.onQueryChange() }
                    },
                    {
                        icon: () => <FilterList />,
                        tooltip: 'Enable/disable filter',
                        isFreeAction: true,
                        onClick: () => {
                            this.setState({ filtering: !this.state.filtering });

                            // after switch filtering off/on
                            if (this.tableRef.current.state.query.filters.length > 0) {
                                if (this.tableRef.current)
                                    this.tableRef.current.onQueryChange()
                            }
                        }
                    }
                ]}
                editable={{
                    onRowAdd: newData =>
                        new Promise((resolve, reject) => {
                            let url = window.API + '/views?userName=' + window.USER;

                            let payload = JSON.parse(JSON.stringify(newData));

                            if (payload.class_id !== CLASS_DEFAULT) {
                                payload.class_id = parseInt(payload.class_id);
                            } else
                                delete payload['class_id'];

                            if (payload.bucket_id !== BUCKET_DEFAULT) 
                                payload.bucket_id = parseInt(payload.bucket_id);
                            else
                                delete payload['bucket_id'];

                            if (payload.filter_id !== FILTER_DEFAULT) 
                                payload.filter_id = parseInt(payload.filter_id);
                            else
                                delete payload['filter_id'];

                            let result_ok = true;
                            fetch(url, {
                                method: 'POST',
                                body: JSON.stringify(payload),
                                headers: {
                                    'Content-Type': 'application/json'
                                }
                            })
                                .then(handleErrors)
                                .catch(error => {
                                    result_ok = false;
                                    window.alert(error.message);
                                    reject();
                                })
                                .then(() => {
                                    result_ok === true ? resolve() : reject();
                                });
                        }),
                    onRowUpdate: (newData, oldData) =>
                        new Promise((resolve, reject) => {

                            let changed = false;
                            let payload = {};

                            if (oldData.icon_name !== newData.icon_name) {
                                payload.icon_name = newData.icon_name;
                                changed = true;
                            }

                            if (oldData.description !== newData.description) {
                                payload.description = newData.description;
                                changed = true;
                            }

                            if (oldData.bucket_id !== newData.bucket_id) {
                                if (newData.bucket_id !== BUCKET_DEFAULT) 
                                    payload.bucket_id = parseInt(newData.bucket_id);
                                else
                                    payload.bucket_id = null;
                                changed = true;
                            }

                            if (oldData.class_id !== newData.class_id) {
                                if (newData.class_id !== CLASS_DEFAULT) {
                                    payload.class_id = parseInt(newData.class_id);
                                } else
                                    payload.class_id = null;

                                changed = true;
                            }

                            if (oldData.columns_id !== newData.columns_id) {
                                payload.columns_id = parseInt(newData.columns_id);
                                changed = true;
                            }

                            if (oldData.filter_id !== newData.filter_id) {
                                if (newData.filter_id !== FILTER_DEFAULT) {
                                    payload.filter_id = parseInt(newData.filter_id);
                                } else
                                    payload.filter_id = null;

                                changed = true;
                            }

                            if (oldData.view_name !== newData.view_name) {
                                payload.view_name = newData.view_name;
                                changed = true;

                                if (payload.view_name.length === 0)
                                    payload.view_name = null;
                            }

                            let result_ok = true;

                            if (changed) {
                                let url = window.API + '/views/' + oldData.view_id + '?userName=' + window.USER;
                                fetch(url, {
                                    method: 'PUT',
                                    body: JSON.stringify(payload),
                                    headers: {
                                        'Content-Type': 'application/json'
                                    }
                                })
                                    .then(handleErrors)
                                    .catch(error => {
                                        result_ok = false;
                                        window.alert(error.message);
                                        reject();
                                    })
                                    .then(() => {
                                        result_ok === true ? resolve() : reject();
                                    });
                            } else
                                reject();
                        }),
                    onRowDelete: oldData =>
                        new Promise((resolve, reject) => {
                            if (oldData.view_id > 0) {
                                let url = window.API + '/views/' + oldData.view_id + '?userName=' + window.USER;
                                fetch(url, { method: 'DELETE' })
                                    .then(handleErrors)
                                    .catch(error => {
                                        window.alert(error.message);
                                        reject();
                                    })
                                    .then(resolve());
                            } else {
                                window.alert("This row can not be removed.");
                                resolve();
                            }

                        }),
                }}
            />
        );
    }
}

export default ViewsTab;