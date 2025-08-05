import { Table } from '@tanstack/react-table';
import {
    Pagination,
    PaginationContent,
    PaginationItem,
    PaginationPrevious,
    PaginationLink,
    PaginationEllipsis,
    PaginationNext,
} from '@/components/ui/pagination';

interface DataTablePaginationProps<TData> {
    table: Table<TData>;
}

// Helper function to calculate the page range
const calculatePageRange = (
    pageIndex: number,
    pageCount: number,
    maxPagesToShow: number
): { startPage: number; endPage: number } => {
    if (pageCount <= maxPagesToShow) {
        return { startPage: 0, endPage: pageCount - 1 };
    }

    const pagesBefore = pageIndex;
    const pagesAfter = pageCount - 1 - pageIndex;

    if (pagesBefore >= 2 && pagesAfter >= 2) {
        // Center the current page
        return { startPage: pageIndex - 2, endPage: pageIndex + 2 };
    } else if (pagesBefore < 2) {
        // Near the beginning
        return { startPage: 0, endPage: maxPagesToShow - 1 };
    } else {
        // Near the end (pagesAfter < 2)
        return {
            startPage: pageCount - maxPagesToShow,
            endPage: pageCount - 1,
        };
    }
};

export function DataTablePagination<TData>({
    table,
}: DataTablePaginationProps<TData>) {
    const pageIndex = table.getState().pagination.pageIndex;
    const pageCount = table.getPageCount();

    const renderPageLinks = () => {
        const pageNumbers = [];
        const maxPagesToShow = 5;

        // Calculate the range of pages to display
        const { startPage, endPage } = calculatePageRange(
            pageIndex,
            pageCount,
            maxPagesToShow
        );

        // Add leading ellipsis if needed
        if (startPage > 0) {
            pageNumbers.push(
                <PaginationItem key="start-ellipsis">
                    <PaginationEllipsis />
                </PaginationItem>
            );
        }

        // Generate page number links
        for (let i = startPage; i <= endPage; i++) {
            pageNumbers.push(
                <PaginationItem key={i} className="hover:cursor-pointer">
                    <PaginationLink
                        onClick={() => table.setPageIndex(i)}
                        isActive={pageIndex === i}>
                        {i + 1}
                    </PaginationLink>
                </PaginationItem>
            );
        }

        // Add trailing ellipsis if needed
        if (endPage < pageCount - 1) {
            pageNumbers.push(
                <PaginationItem key="end-ellipsis">
                    <PaginationEllipsis />
                </PaginationItem>
            );
        }

        return pageNumbers;
    };

    return (
        <div className="flex flex-col justify-center items-center gap-y-3 ">
            <div className="flex-1 text-sm text-muted-foreground">
                {table.getFilteredSelectedRowModel().rows.length} of{' '}
                {table.getFilteredRowModel().rows.length} row(s) selected.
            </div>
            <div className="flex items-center space-x-6 lg:space-x-8">
                <Pagination>
                    <PaginationContent>
                        <PaginationItem
                            className={
                                table.getCanPreviousPage()
                                    ? 'hover:cursor-pointer'
                                    : 'hover:cursor-not-allowed opacity-50' // Added opacity for disabled state
                            }>
                            <PaginationPrevious
                                onClick={() => table.previousPage()}
                                // Disable button semantics if cannot go previous
                                aria-disabled={!table.getCanPreviousPage()}
                                tabIndex={table.getCanPreviousPage() ? 0 : -1}
                            />
                        </PaginationItem>

                        {renderPageLinks()}
                        <PaginationItem
                            className={
                                table.getCanNextPage()
                                    ? 'hover:cursor-pointer'
                                    : 'hover:cursor-not-allowed opacity-50' // Added opacity for disabled state
                            }>
                            <PaginationNext
                                onClick={() => {
                                    if (table.getCanNextPage()) {
                                        table.nextPage();
                                    }
                                }}
                                // Disable button semantics if cannot go next
                                aria-disabled={!table.getCanNextPage()}
                                tabIndex={table.getCanNextPage() ? 0 : -1}
                            />
                        </PaginationItem>
                    </PaginationContent>
                </Pagination>
            </div>
        </div>
    );
}
